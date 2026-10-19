$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$backend = Join-Path $root 'backend'
$envFile = Join-Path $root '.env'

if (-not (Test-Path $envFile)) {
  throw 'Copy .env.example to .env, then set DATABASE_PASSWORD and JWT_SECRET before starting Morrow.'
}

$values = @{}
Get-Content $envFile | ForEach-Object {
  if ($_ -match '^\s*([A-Z_]+)\s*=(.*)$') {
    $values[$matches[1]] = $matches[2].Trim()
  }
}
foreach ($key in $values.Keys) { Set-Item -Path "Env:$key" -Value $values[$key] }

$portMatch = [regex]::Match($values['DATABASE_URL'], ':(\d+)/[^/?]+')
if (-not $portMatch.Success) { throw 'DATABASE_URL must look like jdbc:postgresql://localhost:5434/morrow_db.' }
$dbPort = [int]$portMatch.Groups[1].Value
$dataDir = Join-Path $backend 'postgres-local-data'

$pgCtl = Get-Command pg_ctl.exe -ErrorAction SilentlyContinue
$pgBin = if ($pgCtl) { Split-Path $pgCtl.Source } else { $null }
if (-not $pgBin) {
  $installed = Get-ChildItem 'C:\Program Files\PostgreSQL\*\bin\pg_ctl.exe' -ErrorAction SilentlyContinue |
    Sort-Object { [int](Split-Path (Split-Path (Split-Path $_.FullName -Parent) -Parent) -Leaf) } -Descending |
    Select-Object -First 1
  if ($installed) { $pgBin = Split-Path $installed.FullName }
}
if (-not $pgBin) { throw 'PostgreSQL command-line tools were not found. Install PostgreSQL, then run this script again.' }

if (-not (Test-Path $dataDir)) {
  $passwordFile = Join-Path $backend 'postgres-init-password.tmp'
  try {
    [IO.File]::WriteAllText($passwordFile, $values['DATABASE_PASSWORD'])
    & (Join-Path $pgBin 'initdb.exe') -D $dataDir -U $values['DATABASE_USER'] --auth-local=scram-sha-256 --auth-host=scram-sha-256 --pwfile=$passwordFile --encoding=UTF8
    if ($LASTEXITCODE -ne 0) { throw 'PostgreSQL initialization failed.' }
  } finally {
    Remove-Item -LiteralPath $passwordFile -Force -ErrorAction SilentlyContinue
  }
}

& (Join-Path $pgBin 'pg_ctl.exe') status -D $dataDir *> $null
if ($LASTEXITCODE -ne 0) {
  $dbLog = Join-Path $backend 'postgres-morrow.log'
  & (Join-Path $pgBin 'pg_ctl.exe') start -D $dataDir -l $dbLog -o "-p $dbPort -h 127.0.0.1"
  if ($LASTEXITCODE -ne 0) { throw "PostgreSQL did not start. Check $dbLog." }
}

$env:PGPASSWORD = $values['DATABASE_PASSWORD']
$dbExists = & (Join-Path $pgBin 'psql.exe') -w -h 127.0.0.1 -p $dbPort -U $values['DATABASE_USER'] -d postgres -qAt -c "SELECT 1 FROM pg_database WHERE datname='morrow_db'"
if ($LASTEXITCODE -ne 0) { throw 'PostgreSQL is running but Morrow could not authenticate. Check the DATABASE_* values in .env.' }
if ($dbExists.Trim() -ne '1') {
  & (Join-Path $pgBin 'createdb.exe') -h 127.0.0.1 -p $dbPort -U $values['DATABASE_USER'] morrow_db
  if ($LASTEXITCODE -ne 0) { throw 'Could not create morrow_db.' }
}

$maven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if (-not $maven) { $maven = Get-Command mvn -ErrorAction SilentlyContinue }
$mavenPath = if ($maven) { $maven.Source } else { $null }
if (-not $mavenPath) {
  $wrapperCache = Join-Path $env:USERPROFILE '.m2\wrapper\dists'
  $cachedMaven = Get-ChildItem $wrapperCache -Filter mvn.cmd -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
  if ($cachedMaven) { $mavenPath = $cachedMaven.FullName }
}
if (-not $mavenPath) { throw 'Maven was not found. Install Maven 3.9+ or create a Maven wrapper, then rerun this script.' }

$repo = Join-Path $backend '.m2-repository'
Push-Location $backend
try { & $mavenPath "-Dmaven.repo.local=$repo" spring-boot:run; if ($LASTEXITCODE -ne 0) { throw 'Morrow API stopped with an error.' } }
finally { Pop-Location }
