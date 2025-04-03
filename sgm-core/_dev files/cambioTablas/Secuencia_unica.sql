
-- Postgresql:
CREATE SEQUENCE sgm_app.app_uni_seq
    INCREMENT 1
    START 200000
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 10;
ALTER SEQUENCE sgm_app.app_uni_seq
    OWNER TO sisapp;
	
	
	

	
ALTER TABLE sgm_app.cat_predio ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_predio_edificacion ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_predio_edificacion_prop ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_predio_propietario ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_predio_s4 ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_predio_s6 ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.foto_predio ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.ctlg_catalogo ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_edf_categ_prop ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_edf_prop ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.ctlg_item ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_ente ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.cat_escritura ALTER COLUMN id_escritura SET DEFAULT nextval('sgm_app.app_uni_seq');
ALTER TABLE sgm_app.acl_user ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');


