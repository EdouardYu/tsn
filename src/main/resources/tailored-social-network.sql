CREATE TABLE IF NOT EXISTS "user" (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    firstname VARCHAR NOT NULL,
    lastname VARCHAR NOT NULL,
    username VARCHAR,
    birthday DATE NOT NULL,
    gender VARCHAR NOT NULL ,
    nationality VARCHAR NOT NULL,
    picture VARCHAR,
    bio TEXT,
    visibility VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS interest (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    interest VARCHAR NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT interest_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS validation (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    code CHARACTER(6) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    user_id INTEGER NOT NULL,
    CONSTRAINT validation_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id)
  );

CREATE TABLE IF NOT EXISTS jwt (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    value VARCHAR NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    user_id INTEGER NOT NULL,
    CONSTRAINT jwt_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS follow (
    follower_id INTEGER NOT NULL,
    followed_id INTEGER NOT NULL,
    followed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT follow_user1_fk FOREIGN KEY (follower_id) REFERENCES "user"(id),
    CONSTRAINT follow_user2_fk FOREIGN KEY (followed_id) REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS relationship (
    user1_id INTEGER NOT NULL,
    user2_id INTEGER NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user1_id, user2_id),
    CONSTRAINT relationship_user1_fk FOREIGN KEY (user1_id) REFERENCES "user"(id),
    CONSTRAINT relationship_user2_fk FOREIGN KEY (user2_id) REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS post (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    content TEXT NOT NULL,
    picture VARCHAR,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    visibility VARCHAR NOT NULL,
    user_id INTEGER NOT NULL,
    parent_id INTEGER,
    CONSTRAINT post_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT post_parent_fk FOREIGN KEY (parent_id) REFERENCES post(id)
);

/*
visibility: Visibility level of the post, e.g., "public", "friends", "private"
*/

CREATE TABLE IF NOT EXISTS view (
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT view_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT view_post_fk FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE "like" (
    user_id INTEGER NOT NULL,
    post_id INTEGER NOT NULL,
    liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT like_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT like_post_fk FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE IF NOT EXISTS share (
    user_id INTEGER NOT NULL,
    shared_post_id INTEGER NOT NULL,
    post_id INTEGER UNIQUE NOT NULL,
    PRIMARY KEY (user_id, shared_post_id),
    CONSTRAINT share_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT share_shared_post_fk FOREIGN KEY (shared_post_id) REFERENCES post(id),
    CONSTRAINT share_post_fk FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE IF NOT EXISTS message (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    sender_id INTEGER NOT NULL,
    recipient_id INTEGER NOT NULL,
    content TEXT,
    picture VARCHAR,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    CONSTRAINT message_sender_fk FOREIGN KEY (sender_id) REFERENCES "user"(id),
    CONSTRAINT message_recipient_fk FOREIGN KEY (recipient_id) REFERENCES "user"(id)
);