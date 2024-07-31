--
-- PostgreSQL database dump
--

-- Dumped from database version 15.7 (Debian 15.7-0+deb12u1)
-- Dumped by pg_dump version 15.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: avatars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.avatars (
    id bigint NOT NULL,
    user_id bigint,
    content oid
);


ALTER TABLE public.avatars OWNER TO postgres;

--
-- Name: avatars_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.avatars_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.avatars_id_seq OWNER TO postgres;

--
-- Name: avatars_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.avatars_id_seq OWNED BY public.avatars.id;


--
-- Name: countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.countries (
    id bigint NOT NULL,
    flag_code character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.countries OWNER TO postgres;

--
-- Name: countries_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.countries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.countries_id_seq OWNER TO postgres;

--
-- Name: countries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.countries_id_seq OWNED BY public.countries.id;


--
-- Name: group_messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.group_messages (
    country_id bigint NOT NULL,
    creation_date timestamp(6) without time zone NOT NULL,
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    content character varying(1000)
);


ALTER TABLE public.group_messages OWNER TO postgres;

--
-- Name: group_messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.group_messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_messages_id_seq OWNER TO postgres;

--
-- Name: group_messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.group_messages_id_seq OWNED BY public.group_messages.id;


--
-- Name: participant_countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participant_countries (
    country_id bigint NOT NULL,
    participant_id bigint NOT NULL
);


ALTER TABLE public.participant_countries OWNER TO postgres;

--
-- Name: participants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participants (
    id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.participants OWNER TO postgres;

--
-- Name: participants_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.participants_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.participants_id_seq OWNER TO postgres;

--
-- Name: participants_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.participants_id_seq OWNED BY public.participants.id;


--
-- Name: tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tokens (
    expired boolean NOT NULL,
    revoked boolean NOT NULL,
    id bigint NOT NULL,
    user_id bigint,
    token character varying(255),
    token_type character varying(255),
    CONSTRAINT tokens_token_type_check CHECK (((token_type)::text = 'BEARER'::text))
);


ALTER TABLE public.tokens OWNER TO postgres;

--
-- Name: tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tokens_id_seq OWNER TO postgres;

--
-- Name: tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tokens_id_seq OWNED BY public.tokens.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    user_name character varying(16),
    about character varying(500),
    password character varying(255) NOT NULL,
    role character varying(255),
    user_email character varying(255) NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'USER'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: avatars id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avatars ALTER COLUMN id SET DEFAULT nextval('public.avatars_id_seq'::regclass);


--
-- Name: countries id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries ALTER COLUMN id SET DEFAULT nextval('public.countries_id_seq'::regclass);


--
-- Name: group_messages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages ALTER COLUMN id SET DEFAULT nextval('public.group_messages_id_seq'::regclass);


--
-- Name: participants id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants ALTER COLUMN id SET DEFAULT nextval('public.participants_id_seq'::regclass);


--
-- Name: tokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tokens ALTER COLUMN id SET DEFAULT nextval('public.tokens_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: 16485; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16485');


ALTER LARGE OBJECT 16485 OWNER TO postgres;

--
-- Name: 16679; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16679');


ALTER LARGE OBJECT 16679 OWNER TO postgres;

--
-- Name: 16680; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16680');


ALTER LARGE OBJECT 16680 OWNER TO postgres;

--
-- Name: 16681; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16681');


ALTER LARGE OBJECT 16681 OWNER TO postgres;

--
-- Name: 16682; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16682');


ALTER LARGE OBJECT 16682 OWNER TO postgres;

--
-- Name: 16683; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16683');


ALTER LARGE OBJECT 16683 OWNER TO postgres;

--
-- Name: 16878; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16878');


ALTER LARGE OBJECT 16878 OWNER TO postgres;

--
-- Name: 16879; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16879');


ALTER LARGE OBJECT 16879 OWNER TO postgres;

--
-- Name: 16880; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16880');


ALTER LARGE OBJECT 16880 OWNER TO postgres;

--
-- Name: 16977; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16977');


ALTER LARGE OBJECT 16977 OWNER TO postgres;

--
-- Name: 17075; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17075');


ALTER LARGE OBJECT 17075 OWNER TO postgres;

--
-- Name: 17172; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17172');


ALTER LARGE OBJECT 17172 OWNER TO postgres;

--
-- Name: 17270; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17270');


ALTER LARGE OBJECT 17270 OWNER TO postgres;

--
-- Name: 17271; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17271');


ALTER LARGE OBJECT 17271 OWNER TO postgres;

--
-- Name: 17272; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17272');


ALTER LARGE OBJECT 17272 OWNER TO postgres;

--
-- Name: 17273; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17273');


ALTER LARGE OBJECT 17273 OWNER TO postgres;

--
-- Name: 17274; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17274');


ALTER LARGE OBJECT 17274 OWNER TO postgres;

--
-- Name: 17275; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17275');


ALTER LARGE OBJECT 17275 OWNER TO postgres;

--
-- Name: 17276; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17276');


ALTER LARGE OBJECT 17276 OWNER TO postgres;

--
-- Name: 17277; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17277');


ALTER LARGE OBJECT 17277 OWNER TO postgres;

--
-- Name: 17278; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17278');


ALTER LARGE OBJECT 17278 OWNER TO postgres;

--
-- Name: 17279; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('17279');


ALTER LARGE OBJECT 17279 OWNER TO postgres;

--
-- Data for Name: avatars; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.avatars VALUES (1, 2, 17270);
INSERT INTO public.avatars VALUES (2, 3, 17271);
INSERT INTO public.avatars VALUES (3, 4, 17272);
INSERT INTO public.avatars VALUES (4, 5, 17273);
INSERT INTO public.avatars VALUES (5, 6, 17274);
INSERT INTO public.avatars VALUES (6, 7, 17275);
INSERT INTO public.avatars VALUES (7, 8, 17276);
INSERT INTO public.avatars VALUES (8, 9, 17277);
INSERT INTO public.avatars VALUES (9, 10, 17278);
INSERT INTO public.avatars VALUES (10, 11, 17279);


--
-- Data for Name: countries; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.countries VALUES (1, 'ai', 'Anguilla');
INSERT INTO public.countries VALUES (3, 'af', 'Afghanistan');
INSERT INTO public.countries VALUES (4, 'aw', 'Aruba');
INSERT INTO public.countries VALUES (5, 'ao', 'Angola');
INSERT INTO public.countries VALUES (14, 'al', 'Albania');
INSERT INTO public.countries VALUES (15, 'ax', 'Alland Islands');
INSERT INTO public.countries VALUES (17, 'ad', 'Andorra');
INSERT INTO public.countries VALUES (18, 'ae', 'United Arab Emirates');
INSERT INTO public.countries VALUES (19, 'ar', 'Argentina');
INSERT INTO public.countries VALUES (20, 'au', 'Australia');
INSERT INTO public.countries VALUES (27, 'be', 'Belgium');
INSERT INTO public.countries VALUES (28, 'bi', 'Burundi');
INSERT INTO public.countries VALUES (29, 'bh', 'Bahrain');
INSERT INTO public.countries VALUES (30, 'bj', 'Benin');
INSERT INTO public.countries VALUES (31, 'bs', 'The Bahamas');
INSERT INTO public.countries VALUES (32, 'br', 'Brazil');
INSERT INTO public.countries VALUES (33, 'bm', 'Bermuda');
INSERT INTO public.countries VALUES (34, 'bf', 'Burkina Faso');
INSERT INTO public.countries VALUES (35, 'at', 'Austria');
INSERT INTO public.countries VALUES (36, 'az', 'Azerbaijan');
INSERT INTO public.countries VALUES (37, 'bd', 'Bangladesh');
INSERT INTO public.countries VALUES (38, 'ba', 'Bosnia and Herzegovina');
INSERT INTO public.countries VALUES (39, 'bl', 'Saint Barthelemy');
INSERT INTO public.countries VALUES (40, 'by', 'Belarus');
INSERT INTO public.countries VALUES (41, 'bw', 'Botswana');
INSERT INTO public.countries VALUES (42, 'bz', 'Belize');
INSERT INTO public.countries VALUES (43, 'bo', 'Bolivia');
INSERT INTO public.countries VALUES (44, 'bb', 'Barbados');
INSERT INTO public.countries VALUES (45, 'bn', 'Brunei');
INSERT INTO public.countries VALUES (46, 'ca', 'Canada');
INSERT INTO public.countries VALUES (47, 'cn', 'China');
INSERT INTO public.countries VALUES (48, 'ci', 'Ivory Coast');
INSERT INTO public.countries VALUES (49, 'cm', 'Cameroon');
INSERT INTO public.countries VALUES (50, 'cd', 'Democratic Republic of the Congo');
INSERT INTO public.countries VALUES (51, 'cg', 'Republic of Congo');
INSERT INTO public.countries VALUES (52, 'ck', 'Cook Islands');
INSERT INTO public.countries VALUES (53, 'co', 'Colombia');
INSERT INTO public.countries VALUES (54, 'km', 'Comoros');
INSERT INTO public.countries VALUES (55, 'cv', 'Cape Verde');
INSERT INTO public.countries VALUES (56, 'cr', 'Costa Rica');
INSERT INTO public.countries VALUES (57, 'cu', 'Cuba');
INSERT INTO public.countries VALUES (58, 'cw', 'Curacao');
INSERT INTO public.countries VALUES (59, 'ky', 'Cayman Islands');
INSERT INTO public.countries VALUES (60, 'cy', 'Northern Cyprus');
INSERT INTO public.countries VALUES (65, 'cz', 'Czech Republic');
INSERT INTO public.countries VALUES (66, 'de', 'Germany');
INSERT INTO public.countries VALUES (67, 'er', 'Eritrea');
INSERT INTO public.countries VALUES (68, 'dj', 'Djibouti');
INSERT INTO public.countries VALUES (69, 'dm', 'Dominica');
INSERT INTO public.countries VALUES (70, 'dk', 'Denmark');
INSERT INTO public.countries VALUES (71, 'do', 'Dominican Republic');
INSERT INTO public.countries VALUES (72, 'dz', 'Algeria');
INSERT INTO public.countries VALUES (73, 'ec', 'Ecuador');
INSERT INTO public.countries VALUES (74, 'eg', 'Egypt');
INSERT INTO public.countries VALUES (75, 'es', 'Spain');
INSERT INTO public.countries VALUES (76, 'ee', 'Estonia');
INSERT INTO public.countries VALUES (77, 'et', 'Ethiopia');
INSERT INTO public.countries VALUES (78, 'fi', 'Finland');
INSERT INTO public.countries VALUES (79, 'fj', 'Fiji');
INSERT INTO public.countries VALUES (80, 'fk', 'Falkland Islands');
INSERT INTO public.countries VALUES (81, 'fr', 'France');
INSERT INTO public.countries VALUES (82, 'fo', 'Faroe Islands');
INSERT INTO public.countries VALUES (83, 'fm', 'Federated States of Micronesia');
INSERT INTO public.countries VALUES (84, 'ga', 'Gabon');
INSERT INTO public.countries VALUES (85, 'ge', 'Georgia');
INSERT INTO public.countries VALUES (86, 'gi', 'Gibraltar');
INSERT INTO public.countries VALUES (87, 'am', 'Armenia');
INSERT INTO public.countries VALUES (88, 'as', 'American Samoa');
INSERT INTO public.countries VALUES (89, 'aq', 'Antarctica');


--
-- Data for Name: group_messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.group_messages VALUES (3, '2024-07-29 19:59:38.041965', 108, 4, 'dfhdfngfn');
INSERT INTO public.group_messages VALUES (3, '2024-07-29 19:59:40.214996', 109, 4, 'bdfbfdn');
INSERT INTO public.group_messages VALUES (5, '2024-07-29 19:59:58.945812', 110, 4, 'dfhfdhfgfg');
INSERT INTO public.group_messages VALUES (5, '2024-07-29 20:00:01.115519', 111, 4, 'cbnfgnfgn');
INSERT INTO public.group_messages VALUES (1, '2024-07-29 20:00:16.689855', 112, 4, 'dhtddjtdyjmtyj');
INSERT INTO public.group_messages VALUES (1, '2024-07-29 20:00:20.480257', 113, 4, 'djtdmdmmym');
INSERT INTO public.group_messages VALUES (14, '2024-07-29 21:38:32.305856', 114, 4, 'fjkghv');
INSERT INTO public.group_messages VALUES (3, '2024-07-29 21:44:29.270915', 115, 4, 'ljh');
INSERT INTO public.group_messages VALUES (15, '2024-07-29 21:44:38.506706', 116, 4, 'lkjlhkh');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 09:48:50.148227', 117, 4, 'Привіт
');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 09:49:00.583397', 118, 4, 'лолорлодлаопдл длячмдлячм длчо дялмо  оіждомдля для  я жд жд ож ьж ья ');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 10:50:18.901028', 119, 9, 'nmn,m
');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 10:50:30.341298', 120, 9, '456465465476');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 10:51:30.094771', 121, 9, 'kjhkjhkj
');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 10:51:58.221883', 122, 9, 'jnljkkjn,m
');
INSERT INTO public.group_messages VALUES (3, '2024-07-30 10:54:07.215892', 123, 8, 'jbnjm
');
INSERT INTO public.group_messages VALUES (3, '2024-07-30 10:54:12.584403', 124, 8, 'gjhgjh
');
INSERT INTO public.group_messages VALUES (17, '2024-07-30 10:57:41.197609', 125, 9, ''';
');
INSERT INTO public.group_messages VALUES (17, '2024-07-30 10:57:45.75886', 126, 9, ';lk;lk;
');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 11:02:37.942367', 127, 4, 'kjlj');
INSERT INTO public.group_messages VALUES (87, '2024-07-30 11:22:25.947527', 128, 4, 'Hi ');
INSERT INTO public.group_messages VALUES (89, '2024-07-30 11:26:01.020166', 129, 4, 'kjhlk');
INSERT INTO public.group_messages VALUES (89, '2024-07-30 11:26:05.13293', 130, 4, 'lkjbhlkjbhljh');
INSERT INTO public.group_messages VALUES (5, '2024-07-30 11:26:45.042244', 131, 4, 'lihikjhkjh');
INSERT INTO public.group_messages VALUES (5, '2024-07-30 11:26:46.226672', 132, 4, '');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 11:27:10.37854', 133, 4, 'opipiuh');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 11:27:30.462895', 134, 4, 'mghmfghmfh');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 11:27:33.436888', 135, 4, 'ghmffhm');
INSERT INTO public.group_messages VALUES (3, '2024-07-30 11:27:50.53241', 136, 4, 'lihiuhi');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 11:28:45.521773', 137, 4, 'rijfyukfyu');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 11:28:48.071955', 138, 4, 'tjtydjfhfmkfh');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:00:21.804805', 139, 9, ';lk
');
INSERT INTO public.group_messages VALUES (3, '2024-07-30 12:27:52.167538', 140, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 12:28:03.094999', 141, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:28:09.170276', 142, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (17, '2024-07-30 12:28:13.79924', 143, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (18, '2024-07-30 12:28:26.37469', 144, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (87, '2024-07-30 12:28:33.262699', 145, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (89, '2024-07-30 12:28:50.246763', 146, 9, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno
');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:57:23.81455', 147, 9, '');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:57:24.761997', 148, 9, '');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:57:25.425742', 149, 9, '');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:57:25.920027', 150, 9, '');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 12:57:26.376649', 151, 9, '');
INSERT INTO public.group_messages VALUES (3, '2024-07-30 15:27:02.867555', 152, 4, 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unkno');
INSERT INTO public.group_messages VALUES (14, '2024-07-30 15:30:08.9819', 153, 4, 'Меседж');
INSERT INTO public.group_messages VALUES (5, '2024-07-30 15:33:45.209423', 154, 4, 'Vtctl;
');
INSERT INTO public.group_messages VALUES (89, '2024-07-30 15:38:13.806871', 155, 4, 'fdbfdbfd');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:08:28.855979', 156, 5, 'hey aruba
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:08:44.598741', 157, 5, 'hey hey 
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:09:49.369279', 158, 5, 'asdfasdfasdf
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:11:39.549571', 159, 11, 'hey test 11
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:12:16.109088', 160, 11, 'hey test 222
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:13:12.033371', 161, 5, 'test 33
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:13:36.195502', 162, 11, 'asdfasdf
');
INSERT INTO public.group_messages VALUES (4, '2024-07-30 22:14:07.505697', 163, 5, 'sdfgsxcvxcvbxcvb
');
INSERT INTO public.group_messages VALUES (3, '2024-07-30 22:14:31.256665', 164, 11, 'asfasdfasdfasf
');


--
-- Data for Name: participant_countries; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.participant_countries VALUES (4, 71);
INSERT INTO public.participant_countries VALUES (3, 72);
INSERT INTO public.participant_countries VALUES (5, 73);
INSERT INTO public.participant_countries VALUES (1, 74);
INSERT INTO public.participant_countries VALUES (14, 75);
INSERT INTO public.participant_countries VALUES (15, 76);
INSERT INTO public.participant_countries VALUES (4, 77);
INSERT INTO public.participant_countries VALUES (3, 78);
INSERT INTO public.participant_countries VALUES (14, 79);
INSERT INTO public.participant_countries VALUES (3, 80);
INSERT INTO public.participant_countries VALUES (17, 81);
INSERT INTO public.participant_countries VALUES (87, 82);
INSERT INTO public.participant_countries VALUES (89, 83);
INSERT INTO public.participant_countries VALUES (18, 84);
INSERT INTO public.participant_countries VALUES (87, 85);
INSERT INTO public.participant_countries VALUES (89, 86);
INSERT INTO public.participant_countries VALUES (4, 87);
INSERT INTO public.participant_countries VALUES (4, 88);
INSERT INTO public.participant_countries VALUES (3, 89);


--
-- Data for Name: participants; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.participants VALUES (1, 4);
INSERT INTO public.participants VALUES (2, 4);
INSERT INTO public.participants VALUES (3, 4);
INSERT INTO public.participants VALUES (4, 4);
INSERT INTO public.participants VALUES (5, 4);
INSERT INTO public.participants VALUES (6, 4);
INSERT INTO public.participants VALUES (7, 4);
INSERT INTO public.participants VALUES (8, 4);
INSERT INTO public.participants VALUES (9, 4);
INSERT INTO public.participants VALUES (10, 4);
INSERT INTO public.participants VALUES (11, 4);
INSERT INTO public.participants VALUES (12, 4);
INSERT INTO public.participants VALUES (13, 4);
INSERT INTO public.participants VALUES (14, 4);
INSERT INTO public.participants VALUES (15, 3);
INSERT INTO public.participants VALUES (16, 4);
INSERT INTO public.participants VALUES (17, 3);
INSERT INTO public.participants VALUES (18, 3);
INSERT INTO public.participants VALUES (19, 3);
INSERT INTO public.participants VALUES (20, 3);
INSERT INTO public.participants VALUES (21, 3);
INSERT INTO public.participants VALUES (22, 3);
INSERT INTO public.participants VALUES (23, 3);
INSERT INTO public.participants VALUES (24, 4);
INSERT INTO public.participants VALUES (25, 4);
INSERT INTO public.participants VALUES (26, 4);
INSERT INTO public.participants VALUES (27, 4);
INSERT INTO public.participants VALUES (28, 3);
INSERT INTO public.participants VALUES (29, 4);
INSERT INTO public.participants VALUES (30, 4);
INSERT INTO public.participants VALUES (31, 4);
INSERT INTO public.participants VALUES (32, 4);
INSERT INTO public.participants VALUES (33, 4);
INSERT INTO public.participants VALUES (34, 4);
INSERT INTO public.participants VALUES (35, 4);
INSERT INTO public.participants VALUES (36, 4);
INSERT INTO public.participants VALUES (37, 4);
INSERT INTO public.participants VALUES (38, 4);
INSERT INTO public.participants VALUES (39, 4);
INSERT INTO public.participants VALUES (40, 4);
INSERT INTO public.participants VALUES (41, 4);
INSERT INTO public.participants VALUES (42, 4);
INSERT INTO public.participants VALUES (43, 4);
INSERT INTO public.participants VALUES (44, 4);
INSERT INTO public.participants VALUES (45, 4);
INSERT INTO public.participants VALUES (46, 4);
INSERT INTO public.participants VALUES (47, 4);
INSERT INTO public.participants VALUES (48, 4);
INSERT INTO public.participants VALUES (49, 4);
INSERT INTO public.participants VALUES (50, 4);
INSERT INTO public.participants VALUES (51, 4);
INSERT INTO public.participants VALUES (52, 4);
INSERT INTO public.participants VALUES (53, 4);
INSERT INTO public.participants VALUES (54, 4);
INSERT INTO public.participants VALUES (55, 4);
INSERT INTO public.participants VALUES (56, 4);
INSERT INTO public.participants VALUES (57, 4);
INSERT INTO public.participants VALUES (58, 4);
INSERT INTO public.participants VALUES (59, 4);
INSERT INTO public.participants VALUES (60, 4);
INSERT INTO public.participants VALUES (61, 9);
INSERT INTO public.participants VALUES (62, 4);
INSERT INTO public.participants VALUES (63, 4);
INSERT INTO public.participants VALUES (64, 10);
INSERT INTO public.participants VALUES (65, 10);
INSERT INTO public.participants VALUES (66, 10);
INSERT INTO public.participants VALUES (67, 4);
INSERT INTO public.participants VALUES (68, 4);
INSERT INTO public.participants VALUES (69, 4);
INSERT INTO public.participants VALUES (70, 4);
INSERT INTO public.participants VALUES (71, 4);
INSERT INTO public.participants VALUES (72, 4);
INSERT INTO public.participants VALUES (73, 4);
INSERT INTO public.participants VALUES (74, 4);
INSERT INTO public.participants VALUES (75, 4);
INSERT INTO public.participants VALUES (76, 4);
INSERT INTO public.participants VALUES (77, 9);
INSERT INTO public.participants VALUES (78, 9);
INSERT INTO public.participants VALUES (79, 9);
INSERT INTO public.participants VALUES (80, 8);
INSERT INTO public.participants VALUES (81, 9);
INSERT INTO public.participants VALUES (82, 4);
INSERT INTO public.participants VALUES (83, 4);
INSERT INTO public.participants VALUES (84, 9);
INSERT INTO public.participants VALUES (85, 9);
INSERT INTO public.participants VALUES (86, 9);
INSERT INTO public.participants VALUES (87, 5);
INSERT INTO public.participants VALUES (88, 11);
INSERT INTO public.participants VALUES (89, 11);


--
-- Data for Name: tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tokens VALUES (false, false, 56, 3, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZWdpbmFAZ21haWwuY29tIiwiaWF0IjoxNzIyMjU3NzE1LCJleHAiOjE3MjIzNDQxMTV9.NNdslEvl_n-YrTH7t5wqRwzNy6OVXrT_tJRUVHNPZ4o', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 25, 6, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXNhZmFyeUB0ZXN0LmNvbSIsImlhdCI6MTcyMjA4MzE5MiwiZXhwIjoxNzIyMTY5NTkyfQ.TQNZ6QQIlJVoPWmV_iW3BeDQRZqJVmCnoBg6zrbDyRI', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 70, 2, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b21hc0BpLnVhIiwiaWF0IjoxNzIyMjgwMTQ0LCJleHAiOjE3MjIzNjY1NDR9.21jtGbsA91A7QLtkCO47sCGbJwxExXtmuEZ8h5AsPHs', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 81, 8, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGkudWEiLCJpYXQiOjE3MjIzMzY4MzIsImV4cCI6MTcyMjQyMzIzMn0.Y3gEUNpjv4ePQ0fIkViw_S5s82j6jSrZQmSnYXFDVpM', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 73, 7, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwcm9rb3BlbmtvODlAZ21haWwuY29tIiwiaWF0IjoxNzIyMjgxMDE0LCJleHAiOjE3MjIzNjc0MTR9.StUj-fkIXFBDI8lmh7ox6r92Z5dF5SMmXaEUyAnTWbE', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 82, 9, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b21hczNAaS51YSIsImlhdCI6MTcyMjMzNjg0MiwiZXhwIjoxNzIyNDIzMjQyfQ.JZfeapvj4wHrDkU9CnC9w7gs3iPY9lrZJWoIzXmrR6s', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 84, 10, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1jbGVhbnlAZ21haWwuY29tIiwiaWF0IjoxNzIyMzQ4ODE1LCJleHAiOjE3MjI0MzUyMTV9.I-F1uF-MsoU3QpqG4NSdvAcFWNO1mWxJv699Rd44MHk', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 86, 4, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZWdpQGdtYWlsLmNvbSIsImlhdCI6MTcyMjM1MzM2OCwiZXhwIjoxNzIyNDM5NzY4fQ.T581Q3ZEN2atDqdh2gbxEhkS1fm3Ab5po-fCU1zm0TQ', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 87, 5, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNzIyMzc3MjAyLCJleHAiOjE3MjI0NjM2MDJ9.UVsUG0idqpA8-tjQkKcpA6sPlx4_0eLkTAX3zx02Zzk', 'BEARER');
INSERT INTO public.tokens VALUES (false, false, 89, 11, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LWVtYWlsQHRlc3QuY29tIiwiaWF0IjoxNzIyMzc3NDgzLCJleHAiOjE3MjI0NjM4ODN9.AsQdS2i46rnupRs9Yl8dPIoDES5U7LebSKX3lHl954s', 'BEARER');


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.users VALUES (1, 'admin', NULL, '$2a$10$d7Hf8rbm/IEDVoK0lmped.w6dApZdlCqfyBiHQ8Z1gUzhbN6cTIWW', 'ADMIN', 'admin@2t.com');
INSERT INTO public.users VALUES (2, 'srtfdftg', NULL, '$2a$10$XA/DzlsI.QvR1EbTci7gTOVd2J5YCyganRXQMEQM/f3HiB59y2Mqu', 'USER', 'tomas@i.ua');
INSERT INTO public.users VALUES (3, 'Regina', NULL, '$2a$10$r2pxQtAwmXMy9Xd0AN1kzu2yebojqJMCYmLy5UVyyNnhb0pO4WP3q', 'USER', 'regina@gmail.com');
INSERT INTO public.users VALUES (4, 'Regi', NULL, '$2a$10$Bu5tIDVtd/j6b3.UYEurKe1VC4f9NUR3b4eYbJn45hLubDgxNS3bq', 'USER', 'regi@gmail.com');
INSERT INTO public.users VALUES (5, 'test test ', NULL, '$2a$10$xgxdS1Hbc9MuV6nGPGqsueVCIr6V.2qllP8gyfLCAk9s3jAUjRrjK', 'USER', 'test@test.com');
INSERT INTO public.users VALUES (6, 'safary test', NULL, '$2a$10$6u1eViKSwAURpfF6H/xtR.QuQDg2Tz0Erazn.Hp7gSe4YZ.SENDzS', 'USER', 'test-safary@test.com');
INSERT INTO public.users VALUES (7, 'ol', NULL, '$2a$10$3acqZW6Uts02usFStQ74M.KKparGzQLTjBOlxRvtDBN6MVZmXuF.m', 'USER', 'prokopenko89@gmail.com');
INSERT INTO public.users VALUES (8, 'sad', NULL, '$2a$10$2Nn/pPktsfR2CMMW3mbgleNKTGgbvGy0lw2ghourBtC/6946SmGSK', 'USER', 'test@i.ua');
INSERT INTO public.users VALUES (9, 'andrewasd', NULL, '$2a$10$WcOyE8h0dRt5BL6UbIFF3OZMu0DfM.e/hCwdRIXuOtPP9Itfb7zCS', 'USER', 'tomas3@i.ua');
INSERT INTO public.users VALUES (10, 'Demidas', NULL, '$2a$10$pv2eWVF6DZzi/CvksZX0HutXiHYg7xgGTZyXygW0qbEK8h2Jo7/dC', 'USER', 'demcleany@gmail.com');
INSERT INTO public.users VALUES (11, 'dima test 111', NULL, '$2a$10$rRyFF6Qfw6wsTcQg8SlZJuprxq1uzyprab1uBvo/2Vl8kNUHiz5UK', 'USER', 'test-email@test.com');


--
-- Name: avatars_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.avatars_id_seq', 10, true);


--
-- Name: countries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.countries_id_seq', 89, true);


--
-- Name: group_messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.group_messages_id_seq', 164, true);


--
-- Name: participants_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.participants_id_seq', 89, true);


--
-- Name: tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tokens_id_seq', 89, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 11, true);


--
-- Data for Name: BLOBS; Type: BLOBS; Schema: -; Owner: -
--

BEGIN;

SELECT pg_catalog.lo_open('16485', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004ce49444154785eedd251721b39100451deff54bc191c3b6268b569d312c92d4e7723dfa703a60685bc2c29e0c27f90fe0f86a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c3a2ebb3f8437b33ac7fb09197f10fec67ebb0984300ffe436760c8b8fff16fc88e9f60a8baffd76fca0b976098b2f7c2a7edc44f3c3e2ab96c10f9d6578587ccc62f8b9838c0d8b6f58183f7d849961f1e9cae305fa1b18161fad095ea3b95161f1ad1ae295da9a13169fa82d5eaca72161f1719ae3f51a9a10169f65045eb29bf661f14106e1555be91d169f621c5eb88fc661f11186e2b59b30acea78ed26ba86c5f947e3e53b68191687df002728af5f589c7c1b1ca236c36a8343d4d62c2c8ebd19ce5158a7b038f396384a5586d50c47a9aa4d581c78639ca624c3ea87d394d4232c4ebb3d0e548f61dd75b98f47df8e03d5d3202c8e9ac482bec3ffff469ca918c3ba61323fc61f7a17ce548c61ddb09747f0b7de823315533d2cce19c3580e3c74e0a1030fbd05c7aac4b06e1e4ae4f3f0573c94c7b12a31ac9b27ca30acbf281d16872c06615d6ceb0bc37a8961dd63582f31ac7b0ceb2586754fddb0386149a78775adda9661bdc4b0ee31ace7a1aa8b617d6158cfab50d5d5b01ec5fd8a415517c3fa2fc37a5291aaae86f528ee5709aaba18d66f0ceb616cead4aaae86f528ee57039b3abbaaab613d84e3d5c0a60a547535ac4771bfb3b1a9030f9d81c3d560583fc2a00e3c74120e5783617d8f411d78e83c1cae06c3fa06833af0d0a9385c0d86f5370ceac04367e3703518d65d0ceac0430570b81a0cebcf18d481876ae0703518d61f30a8030f95c1e16a302c6250071eaa84c3d55037ac75465b0ceac0439570b2320ceb5f0ceac043c570b2320ceb86411d78a81e4e568661ddb0a9a7f047f3385919a5c35a6f6c8b8d3c853f1ac6b12a31ac1b36f214fe6818c7aac4b06ed8c853f8a3611cab92ea61ad37b6d50b672ac6b0bae24cc51856579ca99806612ddbfa0d07aac7b05ae240f5f4086bd9d6179ca624c3ea87d394d426ac655b078e52956135c351aaea14d6dabe2dce5158b3b0d6c66d7188da0cab0d0e515bbfb0d6966d7182f25a86b5366b8b97efa06b586b9bb678ed260cab3a5ebb89c661ad0ddae285fbe81dd61add16afda4afbb0d6d0b678c96e2684b5c6b5c5eb353424ac35a82d5eaca73961ad116df14a6d8d0aeb03dfaa095ea3b98161ad866df102fdcd0c6bb56a8b9f3ec2d8b03ef00d8be1e70e323cac55b82d7ee82cf3c3fac0573d153f6ea25dc2fac0177e3b7ed05c7b85f581affd16fc88e9760ceb131f3f807f721b5b87f58939bc8c7f603f86456ce4c7f8437b332c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896227e0136e27846c05a9c760000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16679', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000046549444154785eedd251725439100551ef7f55de99260647109078448fe03e5795f27c82bb5bbacab72505bcf11fa4bfc1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f47e8a5f7437c3fa171bf963fc81fb5c1d167308e04f5ee3c6b0f8f88fe021a6bb2b2cbef6e378a0b96e098b2ffca578b889e687c5572d83079d6578587ccc6278dc41c686c5372c8c471f6166587cbaf27881fe0686c5476b82d7686e54587cab8678a5b6e684c5276a8b17eb6948587c9ce678bd862684c567198197eca67d587c904178d5567a87c5a7188717eea371587c84a178ed260cab3a5ebb89ae6171fed178f90e5a86c5e12fc009caeb171627bf0687a8cdb0dae010b5350b8b635f867314d6292cce7c258e52956135c351aa6a131607be18a729c9b0fae13425f5088bd35e8f03d563582d71a07a1a84c551f50d672ac6b076de7ec6fffe529ca918c3da31ac63d5c3e29ccfaa1cd67bedb60c6bc7b08e19d68e611d2b1d16877c5cf1b0de0bb765583b8675ccb0760ceb98617d023dbd825ff1144e5646ddb038e18358cd0bf8150fe2703518d62758cd0bf8150fe2703518d62758cd0bf8150fe2703518d64e9d7a36385c0d86b56358c70c6bc7b08e19d68e611d33ac1dc33a66583b8675ac68581cef8b18d6b1a261ad1a6d19d631c3da31ac6386b56358c70c6bc7b08e19d68e611d33ac1dc33a66583b8675ccb0760ceb9861ed18d6b1ba61ad026dd50f8b939561583b8675ccb07ea3785b9cac0cc3fa0d84f55ff8b1a770b2324a87b5fab4c5cf3c82635562582f6147bfe0071ec1b12a31acff8135fd807ffa088e5549f5b056b1b6eae04cc51856579ca918c3ea8a3315d320ac655bbfe040f518564b1ca89e1e612ddbfa01a729c9b0fae13425b5096bd9d6371ca52ac36a86a354d529ac757d5b9ca3b06661ad8bdbe210b519561b1ca2b67e61ad2bdbe204e5b50c6b5dd6162fdf41d7b0d6356df1da4d185675bc76138dc35a17b4c50bf7d13bac35ba2d5eb595f661ada16df192dd4c086b8d6b8bd76b6848586b505bbc584f73c25a23dae295da1a15d607be5513bc467303c35a0ddbe205fa9b19d66ad5168f3ec2d8b03ef00d8be17107191ed62adc160f3acbfcb03ef055bf140f37d12d617de00b3f8e079aebaeb03ef0b51fc1434c776358dff1f103f893d7b83aacef98c31fe30fdcc7b0888dbc8c5f7437c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129e21f7e9d57158da6d9fe0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16680', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000052449444154785eedd2518ea43a140451f6bfaad9194f9e925aa3783fe022e97bed389fad328d93384e29e0e01fa4271896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8bfecce283f66658031bf91affc17eb60e8b3904f05f6e63c7b0f8f15fc19758dd5e61f16bbf8e2fb4ae5dc2e217fe557cb915ad1f16bf6a197cd1b52c1e163f66317cdd852c1b16bf61617cf525ac19163f5d79bc407f0b86c58fd604afd1dc5261f15b35c42bb5b54e58fc446df1623d2d12163f4e73bc5e432b84c5cfb2045eb29bf661f1832c84576da57758fc14cbe185fb681c163fc2a278ed260cab3a5ebb89ae6171fea5f1f21db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3061c37f1fc6fe04cc518d6c0702ee3835ec4998a31ac81bddcc4c7bd823315533d2cce99c152eee3135fc1b12a31ace15625f8f10ffe2e8f63556258c3442238725c3bf52c8e5549e9b03864cc5c2273a79ec5c9ca30ac612e119c3a2e1f7c10272bc3b086e93ea60f3e8593956158c3741fd3079fc2c9caa81b16274c9aee63fae083385c0d86354cf7317df0411cae06c31aa6fb983ef8200e5783610d737de0d471f9e0b3385c0d8635ccf53177ea711cae06c31a2612c191e3daa9040e5783610db712c18f3ff8a31771b81a0c6b602637f171efe2703518d6c052eee0b35ec7e16a281a16c70b632cd7f029bf84dbd55034acf3ddb698cc1d7cd6eb385c0d86355c6f05bffce08fdec5e16a30ace15628f8f1077ff4220e5783610d772bc1ef8f0b4772385c0d86354c548223c7b553091cae06c31ae612993bf5380e5783610d7389e0d471f9e0b3385c0d86354cf78183c79db34fe1703518d6301d070e1e77ce3e85c3d55037acf3c5b6be8903678f9bc7bfc4c9ca30ace19b3270f6b879fc4b9cac0cc31abe2c03c78ffb4f98c6c9ca30ace1cb2c70fcb8ff84699cac8cd2619d6fb5f57d1678c231f590bb38562586353cd2c4230fb98563556258c3234de021c7ec73aee35895540feb7cabad7638533186d515672ac6b0bae24cc53408ebb4adffe140f518564b1ca89e1e619db6f50f4e539261f5c3694a6a13d6695b7f7194aa0cab198e5255a7b0ceeddbe21c85350bebdcb82d0e519b61b5c1216aeb17d6b9655b9ca0bc96619d9bb5c5cb77d035ac739bb678ed260cab3a5ebb89c6619d1bb4c50bf7d13bac73e9b678d556da87752eda162fd9cd0a619dcbb5c5eb35b44858e7426df1623dad13d6b9445bbc525b4b85f5c16fd504afd1dc82619d0ddbe205fa5b33acb3555b7cf5252c1bd607bf61317cdd852c1ed659b82dbee85ad60feb835ff557f1e556b44b581ffcc2afe30bad6bafb03ef8b55fc19758dd8e61fde0c70fe0bfdcc6d661fd600e5fe33fd88f61111bb98c0fda9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11fb09577b7e39bcef20000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16681', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004e449444154785eedd26156db30140561ef7f55dd997a42681286125c97ebbc27cdf7936307eb6ab621056cfc83f4130c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b118645bf8ee20fadcdb02ed8c87fe33f58cfd261318700fecb65ac18162fff14fc88d9ad15166ffb74fca079ad12166ff8a5f871339a3f2cde6a19fcd0b94c1e162fb3187eee44a60d8b7758183f7d0a7386c5ab2b8f07e86fc2b078694df018cd4d1516efaa211ea9ad79c2e215b5c583f5344958bc9ce678bc8666088bd732051eb29bf661f14226c2a3b6d23b2c5ec57478e03e1a87c54b98148fdd846155c76337d1352cce3f351ebe83966171f8057082f2fa85c5c997c1216a33ac3638446dcdc2e2d88be11c85750a8b332f89a3546558cd7094aada84c58117c6694a32ac7e384d493dc2e2b4cbe340f518564b1ca89e06617154bde14cc51856579ca918c3ea8a3315533d2ccea9071cab12c36a8c635562588d71ac4a4a87c521f509272bc3b07ae3646518d697b62ff0b997e2646518d6078ce829befc0a9cac8cba6171c23c86b3037fe215385c0d8675c76af6e1af9c8ec3d56058777b72797ce6860f9d8bc3d5605877fb134158dbee1713385c0d867590613d67580719d67386755c91b6385c0d86759c613d6158c719d61345c3e278af806ebec5f7cfc2ed6a281ad6785d5bec6537fed059385c0d86f50163f917fcadb370b81a0ceb8ea5fc23fedc59385c0d86f58e99fcc1e71eec7f328ac3d56058ef0e5472e095040e5783615d20916d5f25075e49e0703518d6c5b1448ebdf5e3385c0d8675712011bcb2ed7b2b81c3d56058170712c12bdbbeb712385c0d75c31a27b6752011bcb2ed7bebc771b2320ceb828d7c57099f7ec3874ec1c9ca30ac0b36f2b4123efa079f3b05272bc3b0de3193377b9eb9c1c3e7e06465940e6b9cd81633f9cee757f08327e0589518d61d4279e2afcf3ffed43938562586f5015af9abaf1ebefdfd341cab92ea618dd3dbba4234db2bba798e331563585d71a6620cab2bce544c83b0866d7dc281ea31ac9638503d3dc21ab6f580d3946458fd709a92da84356ceb0d47a9cab09ae12855750a6b2cdf16e728ac595863e1b638446d86d50687a8ad5f5863c9b63841792dc31a8bb5c5c377d035acb14c5b3c761386551d8fdd44e3b0c6026df1c07df40e6b4cdd168fda4afbb0c6a46df190ddcc10d698ae2d1eafa149c21a13b5c583f5344f58638ab678a4b6a60aeb8a77d5048fd1dc84618d866df100fdcd19d668d5163f7d0ad38675c53b2c869f3b91c9c31a85dbe287ce65feb0ae78ab2fc58f9bd12a615df1864fc70f9ad75a615df1b64fc18f98dd8a61ddf0f203f82f97b1745837cce1bff11facc7b0888decc61f5a9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11bbe6058c6ff0c0a510000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16682', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000046549444154785eedd251725439100551ef7f55de99260647109078448fe03e5795f27c82bb5bbacab72505bcf11fa4bfc1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f47e8a5f7437c3fa171bf963fc81fb5c1d167308e04f5ee3c6b0f8f88fe021a6bb2b2cbef6e378a0b96e098b2ffca578b889e687c5572d83079d6578587ccc6278dc41c686c5372c8c471f6166587cbaf27881fe0686c5476b82d7686e54587cab8678a5b6e684c5276a8b17eb6948587c9ce678bd862684c567198197eca67d587c904178d5567a87c5a7188717eea371587c84a178ed260cab3a5ebb89ae6171fed178f90e5a86c5e12fc009caeb171627bf0687a8cdb0dae010b5350b8b635f867314d6292cce7c258e52956135c351aa6a131607be18a729c9b0fae13425f5088bd35e8f03d563582d71a07a1a84c551f50d672ac6b076de7ec6fffe529ca918c3da31ac63d5c3e29ccfaa1cd67bedb60c6bc7b08e19d68e611d2b1d16877c5cf1b0de0bb765583b8675ccb0760ceb98617d023dbd825ff1144e5646ddb038e18358cd0bf8150fe2703518d62758cd0bf8150fe2703518d62758cd0bf8150fe2703518d64e9d7a36385c0d86b56358c70c6bc7b08e19d68e611d33ac1dc33a66583b8675ac68581cef8b18d6b1a261ad1a6d19d631c3da31ac6386b56358c70c6bc7b08e19d68e611d33ac1dc33a66583b8675ccb0760ceb9861ed18d6b1ba61ad026dd50f8b939561583b8675ccb07ea3785b9cac0cc3fa0d84f55ff8b1a770b2324a87b5fab4c5cf3c82635562582f6147bfe0071ec1b12a31acff8135fd807ffa088e5549f5b056b1b6eae04cc51856579ca918c3ea8a3315d320ac655bbfe040f518564b1ca89e1e612ddbfa01a729c9b0fae13425b5096bd9d6371ca52ac36a86a354d529ac757d5b9ca3b06661ad8bdbe210b519561b1ca2b67e61ad2bdbe204e5b50c6b5dd6162fdf41d7b0d6356df1da4d185675bc76138dc35a17b4c50bf7d13bac35ba2d5eb595f661ada16df192dd4c086b8d6b8bd76b6848586b505bbc584f73c25a23dae295da1a15d607be5513bc467303c35a0ddbe205fa9b19d66ad5168f3ec2d8b03ef00d8be17107191ed62adc160f3acbfcb03ef055bf140f37d12d617de00b3f8e079aebaeb03ef0b51fc1434c776358dff1f103f893d7b83aacef98c31fe30fdcc7b0888dbc8c5f7437c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129e21f7e9d57158da6d9fe0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16683', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000043c49444154785eedd2416e63311003d1dcff54b9990633068280b350a09836d9aab7f4c25fdd5d1f0b30f8d01f8067202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c581096fa3ca57f7437c2fa4b1bf935fdc07dae0e4b7330d04f5ee3c6b0f4f82fa18f98eeaeb0f4da2fa70f9aeb96b0f4c26fa58f9b687e587ad518fad0598687a5c70ca3cf1d646c587ac360faf4116686a5a78ba703f41b18961ead848e516e54587aab423a52ad3961e9896ae9609d8684a5c729a7e3159a10969e65041db24d7d587a904174d42add61e929c6d1817b1487a547184ac72e4158e974ec12ad61e9fa47d3e11b5486a58bbf80ae205e5f58baf26be822b211560d5d44b6b2b074d997d175046b0a4bd77c255d4a2ac22aa34b495513962ef862ba9a4884d5475713a9232c5dedf574417908ab922e284f4158bad4e7f970d28f3d9bae290c61b9e8c79e4dd71486b05cf463cfa66b0a931e96aef3a9b485a7d28f19e8b2925c1dd60fbdbe981fd2652521ac3dc23a101d962ef24d62c3fa0c6e8bb0f608eb0061ed11d601c2da23ac03b961e90adf2739accfd4b6086b8fb00e10d61e611d20ac3dc23a40587b847580b0f608eb0061ed11d601c2da23ac0384b547580742c3d2e5bd15611d080d6b25b5455807086b8fb00e10d61e611d20ac3dc23a40587b847580b0f608eb0061ed11d601c2da23ac0384b547580772c35a316d2587a52b8b41587b847580b0f608eb0061ed11d681e8b056465bb161e9b29210d61e611d20ac3dc23a901ed6ca682b90ae290c61b5d2358521ac56baa63005612ddafa8f2e280f6155d205e5e9086bd1d637ba9a4884d5475713a926ac455bffe852521156195d4aaaa6b0d6f56de93a829585b52e6e4b17918db06ae822b2f585b5ae6c4b5710af32ac75595b3a7c83d6b0d6356de9d825082b9d8e5da238ac75415b3a708feeb0d6e8b674d42af561ada16de9906d2684b5c6b5a5e3151a12d61ad4960ed6694e586b445b3a52ad51613de8ad4ae818e50686b50adbd201facd0c6b55b5a54f1f616c580f7ac330fadc418687b582dbd287ce323fac07bdea5be9e326ba25ac07bdf0cbe983e6ba2bac07bdf64be823a6bb31ac2f7a7c03fde435ae0eeb8be6f06bfa81fb1096d2467e4cffe86e84050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc282c51f6c7d06155de1f4320000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16878', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000052449444154785eedd2518ea43a140451f6bfaad9194f9e925aa3783fe022e97bed389fad328d93384e29e0e01fa4271896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8bfecce283f66658031bf91affc17eb60e8b3904f05f6e63c7b0f8f15fc19758dd5e61f16bbf8e2fb4ae5dc2e217fe557cb915ad1f16bf6a197cd1b52c1e163f66317cdd852c1b16bf61617cf525ac19163f5d79bc407f0b86c58fd604afd1dc5261f15b35c42bb5b54e58fc446df1623d2d12163f4e73bc5e432b84c5cfb2045eb29bf661f1832c84576da57758fc14cbe185fb681c163fc2a278ed260cab3a5ebb89ae6171fea5f1f21db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3061c37f1fc6fe04cc518d6c0702ee3835ec4998a31ac81bddcc4c7bd823315533d2cce99c152eee3135fc1b12a31ace15625f8f10ffe2e8f63556258c3442238725c3bf52c8e5549e9b03864cc5c2273a79ec5c9ca30ac612e119c3a2e1f7c10272bc3b086e93ea60f3e8593956158c3741fd3079fc2c9caa81b16274c9aee63fae083385c0d86354cf7317df0411cae06c31aa6fb983ef8200e5783610d737de0d471f9e0b3385c0d8635ccf53177ea711cae06c31a2612c191e3daa9040e5783610db712c18f3ff8a31771b81a0c6b602637f171efe2703518d6c052eee0b35ec7e16a281a16c70b632cd7f029bf84dbd55034acf3ddb698cc1d7cd6eb385c0d86355c6f05bffce08fdec5e16a30ace15628f8f1077ff4220e5783610d772bc1ef8f0b4772385c0d86354c548223c7b553091cae06c31ae612993bf5380e5783610d7389e0d471f9e0b3385c0d86354cf78183c79db34fe1703518d6301d070e1e77ce3e85c3d55037acf3c5b6be8903678f9bc7bfc4c9ca30ace19b3270f6b879fc4b9cac0cc31abe2c03c78ffb4f98c6c9ca30ace1cb2c70fcb8ff84699cac8cd2619d6fb5f57d1678c231f590bb38562586353cd2c4230fb98563556258c3234de021c7ec73aee35895540feb7cabad7638533186d515672ac6b0bae24cc53408ebb4adffe140f518564b1ca89e1e619db6f50f4e539261f5c3694a6a13d6695b7f7194aa0cab198e5255a7b0ceeddbe21c85350bebdcb82d0e519b61b5c1216aeb17d6b9655b9ca0bc96619d9bb5c5cb77d035ac739bb678ed260cab3a5ebb89c6619d1bb4c50bf7d13bac73e9b678d556da87752eda162fd9cd0a619dcbb5c5eb35b44858e7426df1623dad13d6b9445bbc525b4b85f5c16fd504afd1dc82619d0ddbe205fa5b33acb3555b7cf5252c1bd607bf61317cdd852c1ed659b82dbee85ad60feb835ff557f1e556b44b581ffcc2afe30bad6bafb03ef8b55fc19758dd8e61fde0c70fe0bfdcc6d661fd600e5fe33fd88f61111bb98c0fda9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11fb09577b7e39bcef20000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16879', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000043049444154785eedd2416e5b410c0451ddff54bad904890023e96cc8382db139f59636a03f24eb71008387fe01f81f080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684a59eff4a7fe86e84f59336f26dfa81fb5c1d96e660a09fbcc68d61e9f1df421fb1dd5d61e9b5df4e1fb4d72d61e9853f4a1fb7d1feb0f4aa63e84377591e961e73187dee226bc3d21b0ea64f5f6167587abaf174807c0bc3d2a385d031c2ad0a4b6f1548478ab5272c3d512c1d2cd392b0f438e174bc401bc2d2b3aca043a6890f4b0fb2888e1a253b2c3dc53a3a708ee0b0f4084be9d821086b3a1d3b446a58bafed574f8049161e9e22fa02b182f2f2c5df9357411b311560c5dc46c6161e9b22fa3eb182c292c5df3957429531156185dca543161e9822fa6ab1989b0f2e86a46ca084b57fb768f3fe9bfdf4e17340f61951056574058bad44f9816d6737c5b845542585d845542585dd3c3d2757ec8c0b09eb3db22ac12c2ea22ac12c2ea1a1d962ef2736686f51cdc166195105617619510561761951056d7dcb074851f3536ace7d4b608ab84b0ba08ab84b0ba08ab84b0ba08ab84b0ba08ab84b0ba08ab84b0ba08ab84b0ba08ab84b0ba8686a5cbfb34c2ea1a1ad619d616617511560961751156096175115609617511560961751156096175115609617511560961751156096175cd0deb4c6a6b6c58bab23108ab84b0ba08ab84b0ba08ab84b0ba468775c6b435332c5dd624845542585d845542585dd3c33a63da9a46d7340c61a5d2350d4358a9744dc304847568eb2fbaa079082b922e689e8cb00e6dfd4657331261e5d1d58c1413d6a1ad5f7429531156185dca5449619debdbd2750c1616d6b9b82d5dc46c84154317315b5e58e7cab67405e34586752e6b4b874f901ad6b9a62d1d3b04614da76387080eeb5cd0960e9c233bacb3ba2d1d354a7c5867695b3a649a0d619d756de9788196847516b5a58365da13d659d1968e146b55582f7aab103a46b885619dc0b674807c3bc33a516de9d357581bd68bde70187dee22cbc33a83dbd287eeb23fac17bdea47e9e336ba25ac17bdf0dbe983f6ba2bac17bdf65be823b6bb31ac2f7a7c03fde435ae0eeb8be6f06dfa81fb1096d246caf487ee4658b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c5810162c080b1684050bc2820561c182b0604158b0202c58fc00660ad9950d6a7e9e0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16880', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004b649444154785eedd26152db40100561dfff54b9d9a6625728d4c4c4809f3c33dbdf4f4a32dab77d5952c0857f909ec1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f4ebbbf8437b33ac3fd8c88ff11fec67ebb0984300ffe536760c8b977f0a7ec4747b85c5db3e1d3f68ae5dc2e20dbf143f6ea2f961f156cbe087ce323c2c5e6631fcdc41c686c53b2c8c9f3ec2ccb07875e5f100fd0d0c8b97d6048fd1dca8b078570df1486dcd098b57d4160fd6d390b07839cdf1780d4d088bd732020fd94dfbb0782183f0a8adf40e8b57310e0fdc47e3b0780943f1d84d1856753c76135dc3e2fca3f1f01db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3ea8a331563585d71a6620cab2bce544cf5b038a7dee158951856631cab12c36a8c6355523a2c0ea90f38591986d51b272bc3b0fee3f2293e7d3a4e568661ddc5883ec597cfc2c9caa81b16273c11ab790c7fe52c1cae06c322f6f230fed059385c0d8645ece58a0ffdf5c833691cae06c33a781fca0d9fb8e3f1279f8ec3d5605807dfabeab5385c0d86756058cf62580786f52c86756058cf62580708ebd2a12d0e5743d1b038de8958d6151faa84dbd55034acf5bab6d8d4077ce1d5385c0d86f56face90ebef60a1cae06c3ba8b11ddc737cfc5e16a30ac8730a50ff8c289385c0d86f5656cea2f3e77160e5783617d13b332ac23c3fa3e96f5a2b6385c0d86f52386758f61fd8861dd5337acd5a1ad9787c5c9ca30ac83afc66158f718d6c19712797bf8f1579e8e939561580768e57227173e74c5874ec1c9ca281dd63abd2dc6f230fed0293856258675c05e1ec35f390bc7aac4b088d5fc0fdf3f11c7aaa47a58eb156dbd6144eff0d1d371a6620cab2bce548c6175c5998a6910d6b2ad0f38503d86d51207aaa74758cbb6dee1342519563f9ca6a436612ddbbae228551956331ca5aa4e61adeddbe21c85350b6b6ddc1687a8cdb0dae010b5f50b6b6dd9162728af65586bb3b678f80eba86b5b6698bc76ec2b0aae3b19b681cd6daa02d1eb88fde61add16df1a8adb40f6b0d6d8b87ec6642586b5c5b3c5e4343c25a83dae2c17a9a13d61ad1168fd4d6a8b06e78574df018cd0d0c6b356c8b07e86f6658ab555bfcf411c68675c33b2c869f3bc8f0b056e1b6f8a1b3cc0feb86b7fa52fcb8897609eb86377c3a7ed05c7b8575c3db3e053f62ba1dc37ac3cb0fe0bfdcc6d661bd610e3fc67fb01fc32236f230fed0de0c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a588df1b970195c90b59180000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16977', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000052449444154785eedd2518ea43a140451f6bfaad9194f9e925aa3783fe022e97bed389fad328d93384e29e0e01fa4271896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8bfecce283f66658031bf91affc17eb60e8b3904f05f6e63c7b0f8f15fc19758dd5e61f16bbf8e2fb4ae5dc2e217fe557cb915ad1f16bf6a197cd1b52c1e163f66317cdd852c1b16bf61617cf525ac19163f5d79bc407f0b86c58fd604afd1dc5261f15b35c42bb5b54e58fc446df1623d2d12163f4e73bc5e432b84c5cfb2045eb29bf661f1832c84576da57758fc14cbe185fb681c163fc2a278ed260cab3a5ebb89ae6171fea5f1f21db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3061c37f1fc6fe04cc518d6c0702ee3835ec4998a31ac81bddcc4c7bd823315533d2cce99c152eee3135fc1b12a31ace15625f8f10ffe2e8f63556258c3442238725c3bf52c8e5549e9b03864cc5c2273a79ec5c9ca30ac612e119c3a2e1f7c10272bc3b086e93ea60f3e8593956158c3741fd3079fc2c9caa81b16274c9aee63fae083385c0d86354cf7317df0411cae06c31aa6fb983ef8200e5783610d737de0d471f9e0b3385c0d8635ccf53177ea711cae06c31a2612c191e3daa9040e5783610db712c18f3ff8a31771b81a0c6b602637f171efe2703518d6c052eee0b35ec7e16a281a16c70b632cd7f029bf84dbd55034acf3ddb698cc1d7cd6eb385c0d86355c6f05bffce08fdec5e16a30ace15628f8f1077ff4220e5783610d772bc1ef8f0b4772385c0d86354c548223c7b553091cae06c31ae612993bf5380e5783610d7389e0d471f9e0b3385c0d86354cf78183c79db34fe1703518d6301d070e1e77ce3e85c3d55037acf3c5b6be8903678f9bc7bfc4c9ca30ace19b3270f6b879fc4b9cac0cc31abe2c03c78ffb4f98c6c9ca30ace1cb2c70fcb8ff84699cac8cd2619d6fb5f57d1678c231f590bb38562586353cd2c4230fb98563556258c3234de021c7ec73aee35895540feb7cabad7638533186d515672ac6b0bae24cc53408ebb4adffe140f518564b1ca89e1e619db6f50f4e539261f5c3694a6a13d6695b7f7194aa0cab198e5255a7b0ceeddbe21c85350bebdcb82d0e519b61b5c1216aeb17d6b9655b9ca0bc96619d9bb5c5cb77d035ac739bb678ed260cab3a5ebb89c6619d1bb4c50bf7d13bac73e9b678d556da87752eda162fd9cd0a619dcbb5c5eb35b44858e7426df1623dad13d6b9445bbc525b4b85f5c16fd504afd1dc82619d0ddbe205fa5b33acb3555b7cf5252c1bd607bf61317cdd852c1ed659b82dbee85ad60feb835ff557f1e556b44b581ffcc2afe30bad6bafb03ef8b55fc19758dd8e61fde0c70fe0bfdcc6d661fd600e5fe33fd88f61111bb98c0fda9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11fb09577b7e39bcef20000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17075', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000052449444154785eedd2518ea43a140451f6bfaad9194f9e925aa3783fe022e97bed389fad328d93384e29e0e01fa4271896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8bfecce283f66658031bf91affc17eb60e8b3904f05f6e63c7b0f8f15fc19758dd5e61f16bbf8e2fb4ae5dc2e217fe557cb915ad1f16bf6a197cd1b52c1e163f66317cdd852c1b16bf61617cf525ac19163f5d79bc407f0b86c58fd604afd1dc5261f15b35c42bb5b54e58fc446df1623d2d12163f4e73bc5e432b84c5cfb2045eb29bf661f1832c84576da57758fc14cbe185fb681c163fc2a278ed260cab3a5ebb89ae6171fea5f1f21db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3061c37f1fc6fe04cc518d6c0702ee3835ec4998a31ac81bddcc4c7bd823315533d2cce99c152eee3135fc1b12a31ace15625f8f10ffe2e8f63556258c3442238725c3bf52c8e5549e9b03864cc5c2273a79ec5c9ca30ac612e119c3a2e1f7c10272bc3b086e93ea60f3e8593956158c3741fd3079fc2c9caa81b16274c9aee63fae083385c0d86354cf7317df0411cae06c31aa6fb983ef8200e5783610d737de0d471f9e0b3385c0d8635ccf53177ea711cae06c31a2612c191e3daa9040e5783610db712c18f3ff8a31771b81a0c6b602637f171efe2703518d6c052eee0b35ec7e16a281a16c70b632cd7f029bf84dbd55034acf3ddb698cc1d7cd6eb385c0d86355c6f05bffce08fdec5e16a30ace15628f8f1077ff4220e5783610d772bc1ef8f0b4772385c0d86354c548223c7b553091cae06c31ae612993bf5380e5783610d7389e0d471f9e0b3385c0d86354cf78183c79db34fe1703518d6301d070e1e77ce3e85c3d55037acf3c5b6be8903678f9bc7bfc4c9ca30ace19b3270f6b879fc4b9cac0cc31abe2c03c78ffb4f98c6c9ca30ace1cb2c70fcb8ff84699cac8cd2619d6fb5f57d1678c231f590bb38562586353cd2c4230fb98563556258c3234de021c7ec73aee35895540feb7cabad7638533186d515672ac6b0bae24cc53408ebb4adffe140f518564b1ca89e1e619db6f50f4e539261f5c3694a6a13d6695b7f7194aa0cab198e5255a7b0ceeddbe21c85350bebdcb82d0e519b61b5c1216aeb17d6b9655b9ca0bc96619d9bb5c5cb77d035ac739bb678ed260cab3a5ebb89c6619d1bb4c50bf7d13bac73e9b678d556da87752eda162fd9cd0a619dcbb5c5eb35b44858e7426df1623dad13d6b9445bbc525b4b85f5c16fd504afd1dc82619d0ddbe205fa5b33acb3555b7cf5252c1bd607bf61317cdd852c1ed659b82dbee85ad60feb835ff557f1e556b44b581ffcc2afe30bad6bafb03ef8b55fc19758dd8e61fde0c70fe0bfdcc6d661fd600e5fe33fd88f61111bb98c0fda9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11fb09577b7e39bcef20000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17172', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004b649444154785eedd26152db40100561dfff54b9d9a6625728d4c4c4809f3c33dbdf4f4a32dab77d5952c0857f909ec1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f4ebbbf8437b33ac3fd8c88ff11fec67ebb0984300ffe536760c8b977f0a7ec4747b85c5db3e1d3f68ae5dc2e20dbf143f6ea2f961f156cbe087ce323c2c5e6631fcdc41c686c53b2c8c9f3ec2ccb07875e5f100fd0d0c8b97d6048fd1dca8b078570df1486dcd098b57d4160fd6d390b07839cdf1780d4d088bd732020fd94dfbb0782183f0a8adf40e8b57310e0fdc47e3b0780943f1d84d1856753c76135dc3e2fca3f1f01db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3ea8a331563585d71a6620cab2bce544cf5b038a7dee158951856631cab12c36a8c6355523a2c0ea90f38591986d51b272bc3b0fee3f2293e7d3a4e568661ddc5883ec597cfc2c9caa81b16273c11ab790c7fe52c1cae06c322f6f230fed059385c0d8645ece58a0ffdf5c833691cae06c33a781fca0d9fb8e3f1279f8ec3d5605807dfabeab5385c0d86756058cf62580786f52c86756058cf62580708ebd2a12d0e5743d1b038de8958d6151faa84dbd55034acf5bab6d8d4077ce1d5385c0d86f56face90ebef60a1cae06c3ba8b11ddc737cfc5e16a30ac8730a50ff8c289385c0d86f5656cea2f3e77160e5783617d13b332ac23c3fa3e96f5a2b6385c0d86f52386758f61fd8861dd5337acd5a1ad9787c5c9ca30ac83afc66158f718d6c19712797bf8f1579e8e939561580768e57227173e74c5874ec1c9ca281dd63abd2dc6f230fed0293856258675c05e1ec35f390bc7aac4b088d5fc0fdf3f11c7aaa47a58eb156dbd6144eff0d1d371a6620cab2bce548c6175c5998a6910d6b2ad0f38503d86d51207aaa74758cbb6dee1342519563f9ca6a436612ddbbae228551956331ca5aa4e61adeddbe21c85350b6b6ddc1687a8cdb0dae010b5f50b6b6dd9162728af65586bb3b678f80eba86b5b6698bc76ec2b0aae3b19b681cd6daa02d1eb88fde61add16df1a8adb40f6b0d6d8b87ec6642586b5c5b3c5e4343c25a83dae2c17a9a13d61ad1168fd4d6a8b06e78574df018cd0d0c6b356c8b07e86f6658ab555bfcf411c68675c33b2c869f3bc8f0b056e1b6f8a1b3cc0feb86b7fa52fcb8897609eb86377c3a7ed05c7b8575c3db3e053f62ba1dc37ac3cb0fe0bfdcc6d661bd610e3fc67fb01fc32236f230fed0de0c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a588df1b970195c90b59180000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17270', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004b649444154785eedd26152db40100561dfff54b9d9a6625728d4c4c4809f3c33dbdf4f4a32dab77d5952c0857f909ec1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f4ebbbf8437b33ac3fd8c88ff11fec67ebb0984300ffe536760c8b977f0a7ec4747b85c5db3e1d3f68ae5dc2e20dbf143f6ea2f961f156cbe087ce323c2c5e6631fcdc41c686c53b2c8c9f3ec2ccb07875e5f100fd0d0c8b97d6048fd1dca8b078570df1486dcd098b57d4160fd6d390b07839cdf1780d4d088bd732020fd94dfbb0782183f0a8adf40e8b57310e0fdc47e3b0780943f1d84d1856753c76135dc3e2fca3f1f01db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3ea8a331563585d71a6620cab2bce544cf5b038a7dee158951856631cab12c36a8c6355523a2c0ea90f38591986d51b272bc3b0fee3f2293e7d3a4e568661ddc5883ec597cfc2c9caa81b16273c11ab790c7fe52c1cae06c322f6f230fed059385c0d8645ece58a0ffdf5c833691cae06c33a781fca0d9fb8e3f1279f8ec3d5605807dfabeab5385c0d86756058cf62580786f52c86756058cf62580708ebd2a12d0e5743d1b038de8958d6151faa84dbd55034acf5bab6d8d4077ce1d5385c0d86f56face90ebef60a1cae06c3ba8b11ddc737cfc5e16a30ac8730a50ff8c289385c0d86f5656cea2f3e77160e5783617d13b332ac23c3fa3e96f5a2b6385c0d86f52386758f61fd8861dd5337acd5a1ad9787c5c9ca30ac83afc66158f718d6c19712797bf8f1579e8e939561580768e57227173e74c5874ec1c9ca281dd63abd2dc6f230fed0293856258675c05e1ec35f390bc7aac4b088d5fc0fdf3f11c7aaa47a58eb156dbd6144eff0d1d371a6620cab2bce548c6175c5998a6910d6b2ad0f38503d86d51207aaa74758cbb6dee1342519563f9ca6a436612ddbbae228551956331ca5aa4e61adeddbe21c85350b6b6ddc1687a8cdb0dae010b5f50b6b6dd9162728af65586bb3b678f80eba86b5b6698bc76ec2b0aae3b19b681cd6daa02d1eb88fde61add16df1a8adb40f6b0d6d8b87ec6642586b5c5b3c5e4343c25a83dae2c17a9a13d61ad1168fd4d6a8b06e78574df018cd0d0c6b356c8b07e86f6658ab555bfcf411c68675c33b2c869f3bc8f0b056e1b6f8a1b3cc0feb86b7fa52fcb8897609eb86377c3a7ed05c7b8575c3db3e053f62ba1dc37ac3cb0fe0bfdcc6d661bd610e3fc67fb01fc32236f230fed0de0c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a588df1b970195c90b59180000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17271', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000052449444154785eedd2518ea43a140451f6bfaad9194f9e925aa3783fe022e97bed389fad328d93384e29e0e01fa4271896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8bfecce283f66658031bf91affc17eb60e8b3904f05f6e63c7b0f8f15fc19758dd5e61f16bbf8e2fb4ae5dc2e217fe557cb915ad1f16bf6a197cd1b52c1e163f66317cdd852c1b16bf61617cf525ac19163f5d79bc407f0b86c58fd604afd1dc5261f15b35c42bb5b54e58fc446df1623d2d12163f4e73bc5e432b84c5cfb2045eb29bf661f1832c84576da57758fc14cbe185fb681c163fc2a278ed260cab3a5ebb89ae6171fea5f1f21db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3061c37f1fc6fe04cc518d6c0702ee3835ec4998a31ac81bddcc4c7bd823315533d2cce99c152eee3135fc1b12a31ace15625f8f10ffe2e8f63556258c3442238725c3bf52c8e5549e9b03864cc5c2273a79ec5c9ca30ac612e119c3a2e1f7c10272bc3b086e93ea60f3e8593956158c3741fd3079fc2c9caa81b16274c9aee63fae083385c0d86354cf7317df0411cae06c31aa6fb983ef8200e5783610d737de0d471f9e0b3385c0d8635ccf53177ea711cae06c31a2612c191e3daa9040e5783610db712c18f3ff8a31771b81a0c6b602637f171efe2703518d6c052eee0b35ec7e16a281a16c70b632cd7f029bf84dbd55034acf3ddb698cc1d7cd6eb385c0d86355c6f05bffce08fdec5e16a30ace15628f8f1077ff4220e5783610d772bc1ef8f0b4772385c0d86354c548223c7b553091cae06c31ae612993bf5380e5783610d7389e0d471f9e0b3385c0d86354cf78183c79db34fe1703518d6301d070e1e77ce3e85c3d55037acf3c5b6be8903678f9bc7bfc4c9ca30ace19b3270f6b879fc4b9cac0cc31abe2c03c78ffb4f98c6c9ca30ace1cb2c70fcb8ff84699cac8cd2619d6fb5f57d1678c231f590bb38562586353cd2c4230fb98563556258c3234de021c7ec73aee35895540feb7cabad7638533186d515672ac6b0bae24cc53408ebb4adffe140f518564b1ca89e1e619db6f50f4e539261f5c3694a6a13d6695b7f7194aa0cab198e5255a7b0ceeddbe21c85350bebdcb82d0e519b61b5c1216aeb17d6b9655b9ca0bc96619d9bb5c5cb77d035ac739bb678ed260cab3a5ebb89c6619d1bb4c50bf7d13bac73e9b678d556da87752eda162fd9cd0a619dcbb5c5eb35b44858e7426df1623dad13d6b9445bbc525b4b85f5c16fd504afd1dc82619d0ddbe205fa5b33acb3555b7cf5252c1bd607bf61317cdd852c1ed659b82dbee85ad60feb835ff557f1e556b44b581ffcc2afe30bad6bafb03ef8b55fc19758dd8e61fde0c70fe0bfdcc6d661fd600e5fe33fd88f61111bb98c0fda9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11fb09577b7e39bcef20000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17272', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000052449444154785eedd2518ea43a140451f6bfaad9194f9e925aa3783fe022e97bed389fad328d93384e29e0e01fa4271896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8bfecce283f66658031bf91affc17eb60e8b3904f05f6e63c7b0f8f15fc19758dd5e61f16bbf8e2fb4ae5dc2e217fe557cb915ad1f16bf6a197cd1b52c1e163f66317cdd852c1b16bf61617cf525ac19163f5d79bc407f0b86c58fd604afd1dc5261f15b35c42bb5b54e58fc446df1623d2d12163f4e73bc5e432b84c5cfb2045eb29bf661f1832c84576da57758fc14cbe185fb681c163fc2a278ed260cab3a5ebb89ae6171fea5f1f21db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3061c37f1fc6fe04cc518d6c0702ee3835ec4998a31ac81bddcc4c7bd823315533d2cce99c152eee3135fc1b12a31ace15625f8f10ffe2e8f63556258c3442238725c3bf52c8e5549e9b03864cc5c2273a79ec5c9ca30ac612e119c3a2e1f7c10272bc3b086e93ea60f3e8593956158c3741fd3079fc2c9caa81b16274c9aee63fae083385c0d86354cf7317df0411cae06c31aa6fb983ef8200e5783610d737de0d471f9e0b3385c0d8635ccf53177ea711cae06c31a2612c191e3daa9040e5783610db712c18f3ff8a31771b81a0c6b602637f171efe2703518d6c052eee0b35ec7e16a281a16c70b632cd7f029bf84dbd55034acf3ddb698cc1d7cd6eb385c0d86355c6f05bffce08fdec5e16a30ace15628f8f1077ff4220e5783610d772bc1ef8f0b4772385c0d86354c548223c7b553091cae06c31ae612993bf5380e5783610d7389e0d471f9e0b3385c0d86354cf78183c79db34fe1703518d6301d070e1e77ce3e85c3d55037acf3c5b6be8903678f9bc7bfc4c9ca30ace19b3270f6b879fc4b9cac0cc31abe2c03c78ffb4f98c6c9ca30ace1cb2c70fcb8ff84699cac8cd2619d6fb5f57d1678c231f590bb38562586353cd2c4230fb98563556258c3234de021c7ec73aee35895540feb7cabad7638533186d515672ac6b0bae24cc53408ebb4adffe140f518564b1ca89e1e619db6f50f4e539261f5c3694a6a13d6695b7f7194aa0cab198e5255a7b0ceeddbe21c85350bebdcb82d0e519b61b5c1216aeb17d6b9655b9ca0bc96619d9bb5c5cb77d035ac739bb678ed260cab3a5ebb89c6619d1bb4c50bf7d13bac73e9b678d556da87752eda162fd9cd0a619dcbb5c5eb35b44858e7426df1623dad13d6b9445bbc525b4b85f5c16fd504afd1dc82619d0ddbe205fa5b33acb3555b7cf5252c1bd607bf61317cdd852c1ed659b82dbee85ad60feb835ff557f1e556b44b581ffcc2afe30bad6bafb03ef8b55fc19758dd8e61fde0c70fe0bfdcc6d661fd600e5fe33fd88f61111bb98c0fda9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11fb09577b7e39bcef20000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17273', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000046549444154785eedd251725439100551ef7f55de99260647109078448fe03e5795f27c82bb5bbacab72505bcf11fa4bfc1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f47e8a5f7437c3fa171bf963fc81fb5c1d167308e04f5ee3c6b0f8f88fe021a6bb2b2cbef6e378a0b96e098b2ffca578b889e687c5572d83079d6578587ccc6278dc41c686c5372c8c471f6166587cbaf27881fe0686c5476b82d7686e54587cab8678a5b6e684c5276a8b17eb6948587c9ce678bd862684c567198197eca67d587c904178d5567a87c5a7188717eea371587c84a178ed260cab3a5ebb89ae6171fed178f90e5a86c5e12fc009caeb171627bf0687a8cdb0dae010b5350b8b635f867314d6292cce7c258e52956135c351aa6a131607be18a729c9b0fae13425f5088bd35e8f03d563582d71a07a1a84c551f50d672ac6b076de7ec6fffe529ca918c3da31ac63d5c3e29ccfaa1cd67bedb60c6bc7b08e19d68e611d2b1d16877c5cf1b0de0bb765583b8675ccb0760ceb98617d023dbd825ff1144e5646ddb038e18358cd0bf8150fe2703518d62758cd0bf8150fe2703518d62758cd0bf8150fe2703518d64e9d7a36385c0d86b56358c70c6bc7b08e19d68e611d33ac1dc33a66583b8675ac68581cef8b18d6b1a261ad1a6d19d631c3da31ac6386b56358c70c6bc7b08e19d68e611d33ac1dc33a66583b8675ccb0760ceb9861ed18d6b1ba61ad026dd50f8b939561583b8675ccb07ea3785b9cac0cc3fa0d84f55ff8b1a770b2324a87b5fab4c5cf3c82635562582f6147bfe0071ec1b12a31acff8135fd807ffa088e5549f5b056b1b6eae04cc51856579ca918c3ea8a3315d320ac655bbfe040f518564b1ca89e1e612ddbfa01a729c9b0fae13425b5096bd9d6371ca52ac36a86a354d529ac757d5b9ca3b06661ad8bdbe210b519561b1ca2b67e61ad2bdbe204e5b50c6b5dd6162fdf41d7b0d6356df1da4d185675bc76138dc35a17b4c50bf7d13bac35ba2d5eb595f661ada16df192dd4c086b8d6b8bd76b6848586b505bbc584f73c25a23dae295da1a15d607be5513bc467303c35a0ddbe205fa9b19d66ad5168f3ec2d8b03ef00d8be17107191ed62adc160f3acbfcb03ef055bf140f37d12d617de00b3f8e079aebaeb03ef0b51fc1434c776358dff1f103f893d7b83aacef98c31fe30fdcc7b0888dbc8c5f7437c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129e21f7e9d57158da6d9fe0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17274', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004b649444154785eedd26152db40100561dfff54b9d9a6625728d4c4c4809f3c33dbdf4f4a32dab77d5952c0857f909ec1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f4ebbbf8437b33ac3fd8c88ff11fec67ebb0984300ffe536760c8b977f0a7ec4747b85c5db3e1d3f68ae5dc2e20dbf143f6ea2f961f156cbe087ce323c2c5e6631fcdc41c686c53b2c8c9f3ec2ccb07875e5f100fd0d0c8b97d6048fd1dca8b078570df1486dcd098b57d4160fd6d390b07839cdf1780d4d088bd732020fd94dfbb0782183f0a8adf40e8b57310e0fdc47e3b0780943f1d84d1856753c76135dc3e2fca3f1f01db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3ea8a331563585d71a6620cab2bce544cf5b038a7dee158951856631cab12c36a8c6355523a2c0ea90f38591986d51b272bc3b0fee3f2293e7d3a4e568661ddc5883ec597cfc2c9caa81b16273c11ab790c7fe52c1cae06c322f6f230fed059385c0d8645ece58a0ffdf5c833691cae06c33a781fca0d9fb8e3f1279f8ec3d5605807dfabeab5385c0d86756058cf62580786f52c86756058cf62580708ebd2a12d0e5743d1b038de8958d6151faa84dbd55034acf5bab6d8d4077ce1d5385c0d86f56face90ebef60a1cae06c3ba8b11ddc737cfc5e16a30ac8730a50ff8c289385c0d86f5656cea2f3e77160e5783617d13b332ac23c3fa3e96f5a2b6385c0d86f52386758f61fd8861dd5337acd5a1ad9787c5c9ca30ac83afc66158f718d6c19712797bf8f1579e8e939561580768e57227173e74c5874ec1c9ca281dd63abd2dc6f230fed0293856258675c05e1ec35f390bc7aac4b088d5fc0fdf3f11c7aaa47a58eb156dbd6144eff0d1d371a6620cab2bce548c6175c5998a6910d6b2ad0f38503d86d51207aaa74758cbb6dee1342519563f9ca6a436612ddbbae228551956331ca5aa4e61adeddbe21c85350b6b6ddc1687a8cdb0dae010b5f50b6b6dd9162728af65586bb3b678f80eba86b5b6698bc76ec2b0aae3b19b681cd6daa02d1eb88fde61add16df1a8adb40f6b0d6d8b87ec6642586b5c5b3c5e4343c25a83dae2c17a9a13d61ad1168fd4d6a8b06e78574df018cd0d0c6b356c8b07e86f6658ab555bfcf411c68675c33b2c869f3bc8f0b056e1b6f8a1b3cc0feb86b7fa52fcb8897609eb86377c3a7ed05c7b8575c3db3e053f62ba1dc37ac3cb0fe0bfdcc6d661bd610e3fc67fb01fc32236f230fed0de0c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a588df1b970195c90b59180000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17275', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004e949444154785eedd251721331140551ef7f55ec4c94e314812618673477f29ed4e793b2c2e8aa6f430ab8f11fa4331896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c8b7e1cc53fb437c3ba6323d3f81fec67ebb0984300ffcb6dec18161fff12fc88d5ed15165ffb72fca075ed12165ff85bf1e356b47e587cd532f8a16b593c2c3e6631fcdc852c1b16dfb0307efa12d60c8b4f571e2fd0df8261f1d19ae0359a5b2a2cbe5543bc525beb84c5276a8b17eb6991b0f838cdf17a0dad10169f6509bc6437edc3e2832c84576da577587c8ae5f0c27d340e8b8fb0285ebb09c3aa8ed76ea26b589c7f69bc7c072dc3e2f01be004e5f50b8b936f8343d466586d7088da9a85c5b137c3390aeb141667de1247a9cab09ae12855b5098b036f8cd3946458fd709a927a84c569b7c781ea31ac9638503d0dc2e2a87ac3998a31acae38533186d515672aa67a589c53bfe158951856631cab12c36a8c6355523a2c0ea9bf70b2320cab374e568661fdc7ed33fcd1f7e1646518d6e798d23ff0d8e538591975c3e28417623e4ff1f0e5385c0d86450ce705fc13d7e2703518d61f98cc9b03bfb91287abc1b03e3096a7b9f0a74f7f1cc5e16a30ac0f5f0de5abbf0fe1703518d63b54727b21141e78e1480287abc1b0de1d4be4d8a97371b81a0cebddb1448e9d3a1787abc1b0ee66fa98397b0a0e5743d1b0385ed84c1c33674fc1ed6a281ad6b8b6ad993866ce9e82c3d56058773371cc9c3d0587abc1b0ee66e298397b0a0e578361ddcdc43173f6141cae06c3ba9b8963e6ec29385c0d86753713c7ccd95370b81a0ceb6e268e99b3a7e0703518d6bb637d1c3b752e0e578361bd3b96c8b153e7e27035d40d6b5cdb1612b9bd50090fbc70e4749cac0cc3faf0d54abefafb044e5686617d4028b7a7adf0a74f7f9cc3c9ca30ac3f309637077e73194e5646e9b04699b69ee39fb80ac7aac4b03ec1709ee2e10b71ac4a0ceb73cce71f78ec5a1cab92ea618def6beb8129bde18fbe03672ac6b0bae24cc51856579ca99806610ddbfa0b07aac7b05ae240f5f4086bd8d66f384d4986d50fa729a94d58c3b6de7094aa0cab198e5255a7b0c6f66d718ec29a8535366e8b43d466586d7088dafa8535b66c8b1394d732acb1595bbc7c075dc31adbb4c56b376158d5f1da4d340e6b6cd0162fdc47efb0c6d26df1aaadb40f6b2cda162fd9cd0a618de5dae2f51a5a24acb1505bbc584feb843596688b576a6ba9b01ef8564df01acd2d18d668d8162fd0df9a618d566df1d397b06c580f7cc362f8b90b593cac51b82d7ee85ad60feb81affaadf8712bda25ac07bef0e5f841ebda2bac07bef625f811abdb31ac5ff8f801fc2fb7b17558bf308769fc0ff66358c4465ec63fb437c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129e22783839e86874ba68d0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17276', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004b649444154785eedd26152db40100561dfff54b9d9a6625728d4c4c4809f3c33dbdf4f4a32dab77d5952c0857f909ec1b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b0146158f4ebbbf8437b33ac3fd8c88ff11fec67ebb0984300ffe536760c8b977f0a7ec4747b85c5db3e1d3f68ae5dc2e20dbf143f6ea2f961f156cbe087ce323c2c5e6631fcdc41c686c53b2c8c9f3ec2ccb07875e5f100fd0d0c8b97d6048fd1dca8b078570df1486dcd098b57d4160fd6d390b07839cdf1780d4d088bd732020fd94dfbb0782183f0a8adf40e8b57310e0fdc47e3b0780943f1d84d1856753c76135dc3e2fca3f1f01db40c8bc36f801394d72f2c4ebe0d0e519b61b5c1216a6b1616c7de0ce728ac53589c794b1ca52ac36a86a354d5262c0ebc314e539261f5c3694aea1116a7dd1e07aac7b05ae240f534088ba3ea8a331563585d71a6620cab2bce544cf5b038a7dee158951856631cab12c36a8c6355523a2c0ea90f38591986d51b272bc3b0fee3f2293e7d3a4e568661ddc5883ec597cfc2c9caa81b16273c11ab790c7fe52c1cae06c322f6f230fed059385c0d8645ece58a0ffdf5c833691cae06c33a781fca0d9fb8e3f1279f8ec3d5605807dfabeab5385c0d86756058cf62580786f52c86756058cf62580708ebd2a12d0e5743d1b038de8958d6151faa84dbd55034acf5bab6d8d4077ce1d5385c0d86f56face90ebef60a1cae06c3ba8b11ddc737cfc5e16a30ac8730a50ff8c289385c0d86f5656cea2f3e77160e5783617d13b332ac23c3fa3e96f5a2b6385c0d86f52386758f61fd8861dd5337acd5a1ad9787c5c9ca30ac83afc66158f718d6c19712797bf8f1579e8e939561580768e57227173e74c5874ec1c9ca281dd63abd2dc6f230fed0293856258675c05e1ec35f390bc7aac4b088d5fc0fdf3f11c7aaa47a58eb156dbd6144eff0d1d371a6620cab2bce548c6175c5998a6910d6b2ad0f38503d86d51207aaa74758cbb6dee1342519563f9ca6a436612ddbbae228551956331ca5aa4e61adeddbe21c85350b6b6ddc1687a8cdb0dae010b5f50b6b6dd9162728af65586bb3b678f80eba86b5b6698bc76ec2b0aae3b19b681cd6daa02d1eb88fde61add16df1a8adb40f6b0d6d8b87ec6642586b5c5b3c5e4343c25a83dae2c17a9a13d61ad1168fd4d6a8b06e78574df018cd0d0c6b356c8b07e86f6658ab555bfcf411c68675c33b2c869f3bc8f0b056e1b6f8a1b3cc0feb86b7fa52fcb8897609eb86377c3a7ed05c7b8575c3db3e053f62ba1dc37ac3cb0fe0bfdcc6d661bd610e3fc67fb01fc32236f230fed0de0c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a588df1b970195c90b59180000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17277', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004e449444154785eedd26156db30140561ef7f55dd997a42681286125c97ebbc27cdf7936307eb6ab621056cfc83f4130c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b118645bf8ee20fadcdb02ed8c87fe33f58cfd261318700fecb65ac18162fff14fc88d9ad15166ffb74fca079ad12166ff8a5f871339a3f2cde6a19fcd0b94c1e162fb3187eee44a60d8b7758183f7d0a7386c5ab2b8f07e86fc2b078694df018cd4d1516efaa211ea9ad79c2e215b5c583f5344958bc9ce678bc8666088bd732051eb29bf661f14226c2a3b6d23b2c5ec57478e03e1a87c54b98148fdd846155c76337d1352cce3f351ebe83966171f8057082f2fa85c5c997c1216a33ac3638446dcdc2e2d88be11c85750a8b332f89a3546558cd7094aada84c58117c6694a32ac7e384d493dc2e2b4cbe340f518564b1ca89e06617154bde14cc51856579ca918c3ea8a3315533d2ccea9071cab12c36a8c635562588d71ac4a4a87c521f509272bc3b07ae3646518d697b62ff0b997e2646518d6078ce829befc0a9cac8cba6171c23c86b3037fe215385c0d8675c76af6e1af9c8ec3d56058777b72797ce6860f9d8bc3d5605877fb134158dbee1713385c0d867590613d67580719d67386755c91b6385c0d86759c613d6158c719d61345c3e278af806ebec5f7cfc2ed6a281ad6785d5bec6537fed059385c0d86f50163f917fcadb370b81a0ceb8ea5fc23fedc59385c0d86f58e99fcc1e71eec7f328ac3d56058ef0e5472e095040e5783615d20916d5f25075e49e0703518d6c5b1448ebdf5e3385c0d8675712011bcb2ed7b2b81c3d56058170712c12bdbbeb712385c0d75c31a27b6752011bcb2ed7bebc771b2320ceb828d7c57099f7ec3874ec1c9ca30ac0b36f2b4123efa079f3b05272bc3b0de3193377b9eb9c1c3e7e06465940e6b9cd81633f9cee757f08327e0589518d61d4279e2afcf3ffed43938562586f5015af9abaf1ebefdfd341cab92ea618dd3dbba4234db2bba798e331563585d71a6620cab2bce544c83b0866d7dc281ea31ac9638503d3dc21ab6f580d3946458fd709a92da84356ceb0d47a9cab09ae12855750a6b2cdf16e728ac595863e1b638446d86d50687a8ad5f5863c9b63841792dc31a8bb5c5c377d035acb14c5b3c761386551d8fdd44e3b0c6026df1c07df40e6b4cdd168fda4afbb0c6a46df190ddcc10d698ae2d1eafa149c21a13b5c583f5344f58638ab678a4b6a60aeb8a77d5048fd1dc84618d866df100fdcd19d668d5163f7d0ad38675c53b2c869f3b91c9c31a85dbe287ce65feb0ae78ab2fc58f9bd12a615df1864fc70f9ad75a615df1b64fc18f98dd8a61ddf0f203f82f97b1745837cce1bff11facc7b0888decc61f5a9b6129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b014f11bbe6058c6ff0c0a510000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17278', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c9000004f149444154785eedd2518e2337100451ddff54be1917b680c122fca36e55f65491f13e171a6e3319af2505bcf80f5205c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c3528461d13f77f1a0b319d6bfd8c8d7f81f9ce7e8b0984300ffcb639c18161fff11fc88dd9d15165ffb71fca07d9d12165ff857f1e376b47f587cd536f8a17bd93c2c3e6633fcdc8d6c1b16dfb0317efa16f60c8b4fd71e2f30df8661f1d186e03586db2a2cbed540bcd258fb84c5271a8b179b6993b0f838c3f17a03ed10169f650bbce434e3c3e2836c84571d6576587c8aedf0c2730c0e8b8fb0295e7b08c3ea8ed71e626a589c7f6bbcfc0423c3e2f007e004edcd0b8b931f8343f46658637088de8685c5b10fc3391a9b1416673e1247e9cab086e1285d8d098b031f8cd3b46458f3709a966684c5698fc781fa31ac9138503f03c2e2a8a55ed7f1885fc2999a31acaff0b80771a6660cab000f7d04676aa67b589cb31a1bf9028fcee3589d18d6b538f07be0afc33856278675bf0cfced1b7f94c4b13a691d16870cf8320bfcf91b7f94c4c9da30ac822670c8ebee393770b2360cab20081cf2ba7bce0d9cac0dc3aa0902e7bcbe38ea124ed646dfb0386146610d85475dc2e17a30acb21a0a8fba84c3f560586535141e750987ebc1b02a6ba83ded431cae07c3aa4ca1f6b40f71b81e0cab3285dad33ec4e17a30acca146a4ffb1087ebc1b02a53a83ded431cae07c3aa4ca1f6b40f71b81e9a86c5f1626a53a83ded43dcae87a661ada7daaa4da1f6b40f71b81e0cab3285dad33ec4e17a30acca146a4ffb1087ebc1b0ca52283cea120ed7836195d55078d4251cae07c32aaba1f0a84b385c0f86555303ce797d71d4551cae07c32aa80187bcee9e730f87ebc1b00a82c021afbbe7dcc3e17ae81bd67aa4ad2f83c09fbff147499cac0dc3badf04fef68d3f0ae3646d18d6b52cf07be0aff338591b865586473f8293b5d13aac956f8b75dcc2439fc2b13a31acaff0b86771ac4e0ceb321ef17b385627ddc35af9b686e24ccd18d6549ca919c39a8a33353320ac655bffc381fa31ac9138503f33c25ab6f5174ed39261cdc3695a1a13d6b2adff7094ae0c6b188ed2d5a4b0d6f16d718ec68685b50e6e8b43f46658637088dee685b58e6c8b13b43732ac75585bbcfc0453c35ac7b4c56b0f6158ddf1da430c0e6b1dd0162f3cc7ecb0d6d66df1aaa38c0f6b6dda162f39cd0e61adeddae2f506da24acb5515bbcd84cfb84b5b6688b571a6babb0def85643f01ac36d18d61ad8162f30df9e61ad516df1d3b7b06d586f7cc366f8b91bd93cacd5b82d7ee85ef60feb8daffaabf8713b3a25ac37bef0e3f841fb3a2bac37bef623f811bb3b31ac1f7cfc00fe97c7383aac1fcce16bfc0fce6358c4463ec683ce66588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c45fc012c1a55f708903cef0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('17279', 131072);
SELECT pg_catalog.lowrite(0, '\x89504e470d0a1a0a0000000d49484452000000c8000000c80802000000223a39c90000051f49444154785eedd25156eb3a1005d1cc7f54cccc6f8570f3a0c090a5f838dd52ed4f901ca955974d0ab8f00fd2110c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186456fa3f8a1b519d6151b791a7f603d4b87c51c02f893cb58312c3efe297888d9ad15165ffb743cd0bc56098b2ffc523cdc8ce60f8baf5a060f3a97c9c3e26316c3e34e64dab0f88685f1e85398332c3e5d79bc407f1386c5476b82d7686eaab0f8560df14a6dcd13169fa82d5eaca749c2e2e334c7eb353443587c9629f092ddb40f8b0f32115eb595de61f129a6c30bf7d1382c3ec2a478ed260cab3a5ebb89ae6171fc53e3e53b68191607bf008ea0bc7e6171e4cbe0206a33ac363888da9a85c5612f86e328ac53581cf3923894aa0cab190ea5aa366171c00be3684a32ac7e389a927a84c5d19671f98aff8ee180ea31aca718d69e066171a895bc2aacb7f26d19d6530c6b8f613dc5b0f6540f8be32ce68561bdd56ecbb09e62587b0ceb2986b5a774581c643daf0debad705b86f514c3da6358bf4137976fe9fcfedf1370646518d6cf500cec2dbbfffd341c591975c3e208cf8256f6fcb8f8f3774ec3c1d560585f2094df7d5f8faf9d8383abc1b0be4028377f2eb8fbbcf2341c5c0d86f53f66b21f0ad7fdc375a7e0e06a30ac0f6ce4af4ab8fa1d179d8283abc1b03e0c24822d97c7761d8e83abc1b03e0c24822d97c7761d8e83abc1b0ae18c8638970cf63bb0ec7c1d5605857c37d0c6f3c10075743d1b038bcb0e13e86371e88b3aba16858dbb96d0df731bcf1401c5c0d867535dcc7f0c60371703518d6d5701fc31b0fc4c1d5605857c37d0c6f3c10075783615d0df731bcf1401c5c0d867535dcc7f0c60371703518d6d5701fc31b0fc4c1d5605857e8e3f25822dcf3d8aec371703518d6878144b0e5f2d8aec3717035d40d6b3bb7ad8144b0e5f2d8ae6371646518d60736f257255cfd8e8bf238b2320ceb7fcc643f14aefb87ebf238b2320ceb0b96f2eecf05779f579e83232ba37458dbe96db1945f7d5f8fafa57158951816a1953d3f2efefc9d1370589518d6cf500cec2dbbfffd1c1c5625d5c3da8ab5c545afc3311563585d714cc51856571c53310dc2da6ceb1b0ea81ec36a8903aaa747589b6d7dc2d1946458fd703425b5096bb3ad771c4a5586d50c875255a7b0b6e5dbe2380a6b16d6b6705b1c446d86d50607515bbfb0b625dbe208ca6b19d6b6585bbc7c075dc3da96698bd76ec2b0aae3b59b681cd6b6405bbc701fbdc3daa66e8b576da57d58dba46df192ddcc10d6365d5bbc5e439384b54dd4162fd6d33c616d53b4c52bb5355558377cab26788de6260c6b6bd8162fd0df9c616daddae2d1a7306d58377cc36278dc894c1ed656b82d1e742ef38775c3577d291e6e46ab8475c3173e1d0f34afb5c2bae16b9f828798dd8a61ddf1f103f893cb583aac3be6f034fec07a0c8bd8c8c3f8a1b51996220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b1186a508c352846129c2b01461588a302c451896220c4b11ff01af99d3f78ba5d2dc0000000049454e44ae426082');
SELECT pg_catalog.lo_close(0);

COMMIT;

--
-- Name: avatars avatars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avatars
    ADD CONSTRAINT avatars_pkey PRIMARY KEY (id);


--
-- Name: avatars avatars_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avatars
    ADD CONSTRAINT avatars_user_id_key UNIQUE (user_id);


--
-- Name: countries countries_flag_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_flag_code_key UNIQUE (flag_code);


--
-- Name: countries countries_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_name_key UNIQUE (name);


--
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (id);


--
-- Name: group_messages group_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT group_messages_pkey PRIMARY KEY (id);


--
-- Name: participants participants_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_pkey PRIMARY KEY (id);


--
-- Name: tokens tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: tokens fk2dylsfo39lgjyqml2tbe0b0ss; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT fk2dylsfo39lgjyqml2tbe0b0ss FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: group_messages fkcy0dbprqmdaqde3w09gt3x8v9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT fkcy0dbprqmdaqde3w09gt3x8v9 FOREIGN KEY (country_id) REFERENCES public.countries(id);


--
-- Name: avatars fkdh0goytewcg1geffkf1clp4kh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avatars
    ADD CONSTRAINT fkdh0goytewcg1geffkf1clp4kh FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: participants fkghixrahoj1s8cloinfx8lyeqa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT fkghixrahoj1s8cloinfx8lyeqa FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: group_messages fkn5qquaksoym7avx54ske9b885; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT fkn5qquaksoym7avx54ske9b885 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: participant_countries fkrxvum8hw9u331naotjw7133on; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant_countries
    ADD CONSTRAINT fkrxvum8hw9u331naotjw7133on FOREIGN KEY (participant_id) REFERENCES public.participants(id);


--
-- Name: participant_countries fksaecpxdva984dnw3lkagxwy2o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant_countries
    ADD CONSTRAINT fksaecpxdva984dnw3lkagxwy2o FOREIGN KEY (country_id) REFERENCES public.countries(id);


--
-- PostgreSQL database dump complete
--

