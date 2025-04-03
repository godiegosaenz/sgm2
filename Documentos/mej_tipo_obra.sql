/*
 Navicat Premium Data Transfer

 Source Server         : 186.42.225.198
 Source Server Type    : PostgreSQL
 Source Server Version : 90602
 Source Host           : 186.42.225.198:5432
 Source Catalog        : sgmsanvicente
 Source Schema         : sgm_mejoras

 Target Server Type    : PostgreSQL
 Target Server Version : 90602
 File Encoding         : 65001

 Date: 24/01/2019 10:39:13
*/


-- ----------------------------
-- Table structure for mej_tipo_obra
-- ----------------------------
DROP TABLE IF EXISTS "sgm_mejoras"."mej_tipo_obra";
CREATE TABLE "sgm_mejoras"."mej_tipo_obra" (
  "id" int8 NOT NULL DEFAULT nextval('"sgm_mejoras".mej_tipo_obra_id_seq'::regclass),
  "descripcion" varchar(250) COLLATE "pg_catalog"."default",
  "estado" bool,
  "usuario" int8,
  "fecha_ingreso" timestamp(6)
)
;

-- ----------------------------
-- Records of mej_tipo_obra
-- ----------------------------
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (1, 'APERTURA, PAVIMENTACIÓN, ENSANCHE Y CONSTRUCCIÓN DE VÍAS DE TODA CLASE', 't', 9, '2017-09-13 00:11:35.297284');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (2, 'OBRAS DE ALCANTARILLADO', 't', 9, '2017-09-13 00:11:56.479325');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (3, 'OTRAS OBRAS QUE LAS MUNICIPALIDADES O DISTRICTOS DETERMINEN  MEDIANTE ORDENANZAS', 't', 9, '2017-09-13 00:13:17.323647');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (4, 'REPAVIMENTACIÓN URBANA', 't', 9, '2017-09-21 16:51:10.61252');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (6, 'CONSTRUCCIÓN Y AMPLIACIÓN DE OBRAS Y SISTEMAS DE AGUA POTABLE', 't', 9, '2017-09-21 16:57:12.43964');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (7, 'DESECACIÓN DE PANTANOS Y RELLENO DE QUEBRADAS', 't', 9, '2017-09-21 16:59:40.057953');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (8, 'PLAZAS, PARQUES Y JARDINES', 't', 9, '2017-09-21 17:00:08.123427');
INSERT INTO "sgm_mejoras"."mej_tipo_obra" VALUES (5, 'ACERAS Y CERCAS - OBRAS DE SOTERRAMIENTO Y ADOSAMIENTO DE LAS REDES PARA LA PRESTACIÓN DE SERVICIOS DE TELECOMUNICACIONES (AUDIO Y VIDEO) POR SUSCRIPCIÓN Y SIMILARES.', 't', 9, '2017-09-21 16:56:20.878483');

-- ----------------------------
-- Primary Key structure for table mej_tipo_obra
-- ----------------------------
ALTER TABLE "sgm_mejoras"."mej_tipo_obra" ADD CONSTRAINT "pk_tipo_obra" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table mej_tipo_obra
-- ----------------------------
ALTER TABLE "sgm_mejoras"."mej_tipo_obra" ADD CONSTRAINT "fk_usuario_acl_user" FOREIGN KEY ("usuario") REFERENCES "sgm_app"."acl_user" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
