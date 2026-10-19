CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  email VARCHAR(180) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  profile_image_url VARCHAR(1000),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE categories (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name VARCHAR(80) NOT NULL,
  description VARCHAR(500),
  color VARCHAR(32) NOT NULL DEFAULT '#dce8df',
  icon VARCHAR(64) NOT NULL DEFAULT 'Flower2',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_categories_user_name UNIQUE(user_id, name)
);
CREATE TABLE tasks (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
  title VARCHAR(180) NOT NULL,
  description VARCHAR(3000),
  status VARCHAR(20) NOT NULL DEFAULT 'TODO' CHECK(status IN ('TODO','IN_PROGRESS','COMPLETED')),
  priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK(priority IN ('LOW','MEDIUM','HIGH','URGENT')),
  start_date DATE,
  due_date DATE,
  completed_at TIMESTAMPTZ,
  time_of_day VARCHAR(24) NOT NULL DEFAULT 'Whenever',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_tasks_user_status ON tasks(user_id,status);
CREATE INDEX idx_tasks_user_due ON tasks(user_id,due_date);
CREATE INDEX idx_tasks_user_created ON tasks(user_id,created_at DESC);
CREATE TABLE task_tags (
  id BIGSERIAL PRIMARY KEY,
  task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  name VARCHAR(60) NOT NULL
);
CREATE INDEX idx_task_tags_task ON task_tags(task_id);
CREATE TABLE notifications (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  task_id BIGINT REFERENCES tasks(id) ON DELETE SET NULL,
  title VARCHAR(180) NOT NULL,
  message VARCHAR(1000) NOT NULL,
  type VARCHAR(32) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_notifications_user_created ON notifications(user_id,created_at DESC);
CREATE INDEX idx_notifications_unread ON notifications(user_id,is_read);
CREATE TABLE task_activities (
  id BIGSERIAL PRIMARY KEY,
  task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  action VARCHAR(80) NOT NULL,
  details VARCHAR(1000),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_activity_user_created ON task_activities(user_id,created_at DESC);
CREATE INDEX idx_activity_task_created ON task_activities(task_id,created_at DESC);
