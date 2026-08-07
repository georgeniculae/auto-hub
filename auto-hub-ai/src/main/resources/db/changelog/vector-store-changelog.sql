-- liquibase formatted sql

-- changeset George Niculae:1
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- changeset George Niculae:2
CREATE TABLE IF NOT EXISTS public.vector_store
(
    id        UUID DEFAULT uuid_generate_v4() NOT NULL,
    content   TEXT,
    metadata  JSON,
    embedding VECTOR(768),
    CONSTRAINT pk_vector_store PRIMARY KEY (id)
);

-- changeset George Niculae:3
CREATE INDEX IF NOT EXISTS idx_vector_store_embedding
    ON public.vector_store USING HNSW (embedding vector_cosine_ops);
