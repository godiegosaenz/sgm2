


alter table sgm_app.cat_predio  drop  column nombre_pueblo_etnia  ;
alter table sgm_app.cat_predio  add   nombre_pueblo_etnia number (19,2);


INSERT into ctlg_catalogo  ( id , nombre) VALUES (3233, 'predio.pueblo_etnia' );
commit;
INSERT INTO sgm_app.ctlg_catalogo  ( id , nombre) VALUES (3234, 'predio.formaadquisicion');
commit;

INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3238, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'NO TIENE', 'no_tiene', 1);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3239, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'ANDOAS', 'andoas', 2);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3240, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'COFANES', 'cofanes', 3);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3241, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'HUARANI', 'huarani', 4);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3242, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'QUICHUAS', 'quichuas', 5);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3243, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'SECOYAS', 'secoyas', 6);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3244, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'SHUAR', 'shuar', 7);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3245, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'SIONAS', 'sionas', 8);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3246, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'ZAPAROS', 'zaparos', 9);
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (3247, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'OTRA', 'otra_(especificar)', 10);
commit;



INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3248,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'ADJUDICACIÓN', 'adjudicacion', 1 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3249,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'COMPRAVENTA', 'compraventa',  2 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3250,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'DONACIÓN', 'donacion', 3 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3251,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'HERENCIA', 'herencia',  4 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3252,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'PARTICIÓN', 'particion',   5 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3253,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'PERMUTA', 'permuta',   6 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3254,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'POSESIÓN', 'posesion',  7 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3255,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'REMATE', 'remate',  8 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3257,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'PRESCRIPCIÓN', 'prescripcion',   9 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3256,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'REGULARIZACIÓN DE EXCEDENTES O DIFERENCIAS', 'regula_escedentes',   10 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3258,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'CESION DE ÁREAS', 'cesion_areas',  11 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3259,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'RESCILIACIÓN', 'resciliacion',  12 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3260,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'POSESIÓN EFECTIVA', 'posesion_efectiva',  13 ); 
commit;
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (3261,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'TESTAMENTO', 'testamento',  14 ); 
commit;






UPDATE "SGM_APP"."ACL_USER" SET FIRMA = NULL ;
COMMIT;
DELETE FROM PE_FIRMA ;
COMMIT;
DELETE FROM ACL_USER_HAS_ROL;
COMMIT;
UPDATE CAT_PREDIO SET USUARIO_CREADOR = NULL;
commit;
UPDATE RESP3_CAT_PREDIO SET USUARIO_CREADOR = NULL;
commit;
DELETE FROM ACL_USER;
COMMIT;
INSERT INTO ACL_USER (id, es_super_user, sis_enabled, user_is_director, usuario, pass) values
(6, 1,1,0,'admin', 'd2c184c3c80d6abe80ed286d697c7d2f064a23cab0b0854089964ab77c28a8e7329f6f1163f9ad73b77b46a91a53dce02936a668feaf7d25a8b1da0854770e31');
commit;
UPDATE ACL_USER SET id = sgm_app.app_uni_seq.NEXTVAL;
COMMIT;
UPDATE CAT_TIPOS_DOMINIO SET USUARIO_INGRESO = NULL;
COMMIT;
INSERT INTO ACL_USER_HAS_ROL (ACL_USER, ACL_ROL) VALUES (6,9);
COMMIT;


