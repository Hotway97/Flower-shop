SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA IF NOT EXISTS public;
ALTER SCHEMA public OWNER TO pg_database_owner;

SET default_tablespace = '';
SET default_table_access_method = heap;

CREATE TABLE public.cart_item (
                                  id bigint NOT NULL,
                                  quantity integer NOT NULL,
                                  cart_id bigint NOT NULL,
                                  product_id bigint NOT NULL
);
ALTER TABLE public.cart_item OWNER TO postgres;

CREATE SEQUENCE public.cart_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.cart_item_id_seq OWNER TO postgres;
ALTER SEQUENCE public.cart_item_id_seq OWNED BY public.cart_item.id;

CREATE TABLE public.carts (
                              id bigint NOT NULL,
                              total_price bigint NOT NULL,
                              user_id bigint
);
ALTER TABLE public.carts OWNER TO postgres;

CREATE SEQUENCE public.carts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.carts_id_seq OWNER TO postgres;
ALTER SEQUENCE public.carts_id_seq OWNED BY public.carts.id;

CREATE TABLE public.chat (
                             chat_id bigint NOT NULL,
                             chat_name character varying(45) NOT NULL,
                             created_at timestamp(6) without time zone NOT NULL,
                             user_id bigint
);
ALTER TABLE public.chat OWNER TO postgres;

CREATE SEQUENCE public.chat_chat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.chat_chat_id_seq OWNER TO postgres;
ALTER SEQUENCE public.chat_chat_id_seq OWNED BY public.chat.chat_id;

CREATE TABLE public.images (
                               id bigint NOT NULL,
                               bytes bytea,
                               content_type character varying(255),
                               name character varying(255),
                               original_file_name character varying(255),
                               preview_image boolean NOT NULL,
                               size bigint,
                               product_id bigint
);
ALTER TABLE public.images OWNER TO postgres;

CREATE SEQUENCE public.images_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.images_id_seq OWNER TO postgres;
ALTER SEQUENCE public.images_id_seq OWNED BY public.images.id;

CREATE TABLE public.message (
                                message_id bigint NOT NULL,
                                content text NOT NULL,
                                is_ai_response boolean NOT NULL,
                                "timestamp" timestamp(6) without time zone NOT NULL,
                                chat_id bigint NOT NULL,
                                user_id bigint
);
ALTER TABLE public.message OWNER TO postgres;

CREATE SEQUENCE public.message_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.message_message_id_seq OWNER TO postgres;
ALTER SEQUENCE public.message_message_id_seq OWNED BY public.message.message_id;

CREATE TABLE public.order_product (
                                      id bigint NOT NULL,
                                      quantity integer NOT NULL,
                                      order_id bigint NOT NULL,
                                      product_id bigint NOT NULL
);
ALTER TABLE public.order_product OWNER TO postgres;

CREATE SEQUENCE public.order_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.order_product_id_seq OWNER TO postgres;
ALTER SEQUENCE public.order_product_id_seq OWNED BY public.order_product.id;

CREATE TABLE public.order_table (
                                    order_id bigint NOT NULL,
                                    created_at timestamp(6) without time zone NOT NULL,
                                    status character varying(45) NOT NULL,
                                    user_id bigint NOT NULL,
                                    total_amount bigint DEFAULT 0 NOT NULL
);
ALTER TABLE public.order_table OWNER TO postgres;

CREATE SEQUENCE public.order_table_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.order_table_order_id_seq OWNER TO postgres;
ALTER SEQUENCE public.order_table_order_id_seq OWNED BY public.order_table.order_id;

CREATE TABLE public.products (
                                 id bigint NOT NULL,
                                 date_of_created timestamp without time zone,
                                 description character varying(255),
                                 preview_image_id bigint,
                                 price integer,
                                 title character varying(255),
                                 user_id bigint
);
ALTER TABLE public.products OWNER TO postgres;

CREATE SEQUENCE public.products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.products_id_seq OWNER TO postgres;
ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;

CREATE TABLE public.user_role (
                                  user_id bigint NOT NULL,
                                  roles character varying(255)
);
ALTER TABLE public.user_role OWNER TO postgres;

CREATE TABLE public.users (
                              id bigint NOT NULL,
                              active boolean NOT NULL,
                              email character varying(255),
                              name character varying(255),
                              password character varying(1000),
                              phone_number character varying(255),
                              avatar_id bigint
);
ALTER TABLE public.users OWNER TO postgres;

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.users_id_seq OWNER TO postgres;
ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;

ALTER TABLE ONLY public.cart_item ALTER COLUMN id SET DEFAULT nextval('public.cart_item_id_seq'::regclass);
ALTER TABLE ONLY public.carts ALTER COLUMN id SET DEFAULT nextval('public.carts_id_seq'::regclass);
ALTER TABLE ONLY public.chat ALTER COLUMN chat_id SET DEFAULT nextval('public.chat_chat_id_seq'::regclass);
ALTER TABLE ONLY public.images ALTER COLUMN id SET DEFAULT nextval('public.images_id_seq'::regclass);
ALTER TABLE ONLY public.message ALTER COLUMN message_id SET DEFAULT nextval('public.message_message_id_seq'::regclass);
ALTER TABLE ONLY public.order_product ALTER COLUMN id SET DEFAULT nextval('public.order_product_id_seq'::regclass);
ALTER TABLE ONLY public.order_table ALTER COLUMN order_id SET DEFAULT nextval('public.order_table_order_id_seq'::regclass);
ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);
ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);

ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT cart_item_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.carts
    ADD CONSTRAINT carts_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.chat
    ADD CONSTRAINT chat_pkey PRIMARY KEY (chat_id);
ALTER TABLE ONLY public.chat
    ADD CONSTRAINT chat_user_id_key UNIQUE (user_id);
ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_pkey PRIMARY KEY (message_id);
ALTER TABLE ONLY public.order_product
    ADD CONSTRAINT order_product_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT order_table_pkey PRIMARY KEY (order_id);
ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.carts
    ADD CONSTRAINT uk_64t7ox312pqal3p7fg9o503c2 UNIQUE (user_id);
ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);
ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk2lttmx8vn9eecykig5sch3v0h FOREIGN KEY (avatar_id) REFERENCES public.images(id);
ALTER TABLE ONLY public.chat
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.carts
    ADD CONSTRAINT fkb5o626f86h46m4s7ms6ginnop FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.products
    ADD CONSTRAINT fkdb050tk37qryv15hd932626th FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT fkfc3ru564ptuisgtaklrqcw6r7 FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.images
    ADD CONSTRAINT fkghwsjbjo7mg3iufxruvq6iu3q FOREIGN KEY (product_id) REFERENCES public.products(id);
ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fkj345gk1bovqvfame88rcx7yyx FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT fklqwuo55w1gm4779xcu3t4wnrd FOREIGN KEY (cart_id) REFERENCES public.carts(id);
ALTER TABLE ONLY public.message
    ADD CONSTRAINT fkmejd0ykokrbuekwwgd5a5xt8a FOREIGN KEY (chat_id) REFERENCES public.chat(chat_id);
ALTER TABLE ONLY public.order_product
    ADD CONSTRAINT fko6helt0ucmegaeachjpx40xhe FOREIGN KEY (product_id) REFERENCES public.products(id);
ALTER TABLE ONLY public.order_product
    ADD CONSTRAINT fkp9qp59hyy3jry2n6c0ikfmq7b FOREIGN KEY (order_id) REFERENCES public.order_table(order_id);
ALTER TABLE ONLY public.message
    ADD CONSTRAINT fkpdrb79dg3bgym7pydlf9k3p1n FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT fkqkqmvkmbtiaqn2nfqf25ymfs2 FOREIGN KEY (product_id) REFERENCES public.products(id);

INSERT INTO public.users (
    id,
    active,
    email,
    name,
    password,
    phone_number,
    avatar_id
) VALUES (
             1,
             true,
             'admin@mail.ru',
             'admin',
             '$2a$08$73B07faPCdygCB5q7FC2qO0tobjvm7pcqIT3fjPd6TGm4sExsQ1yG',
             '+79258802626',
             NULL
         );

INSERT INTO public.user_role (user_id, roles) VALUES
                                           (1, 'ROLE_ADMIN'),
                                           (1, 'ROLE_USER');