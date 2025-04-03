SELECT p.id, p.descripcion, cp.cod_nac FROM app1.cat_parroquia p 
INNER JOIN censocat.parroquia cp ON cp.id=p.id;l

UPDATE app1.cat_parroquia p SET codigo_parroquia=A.cod_nac
FROM (SELECT p.id, p.descripcion, cp.cod_nac FROM app1.cat_parroquia p 
INNER JOIN censocat.parroquia cp ON cp.id=p.id) AS A
WHERE A.id=p.id;