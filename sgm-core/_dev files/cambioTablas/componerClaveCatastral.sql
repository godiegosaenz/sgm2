UPDATE SGM_APP.CAT_PREDIO SET CLAVE_CAT = LPAD(provincia::TEXT, 2, '0') 
|| LPAD(canton::TEXT, 2, '0') || LPAD(parroquia::TEXT, 2, '0') 
|| LPAD(zona::TEXT, 2, '0') || LPAD(sector::TEXT, 2, '0') 
|| LPAD(mz::TEXT, 3, '0') || LPAD(solar::TEXT, 3, '0') 
|| LPAD(bloque::TEXT, 3, '0') || LPAD(piso::TEXT, , '0') 
|| LPAD(unidad::TEXT, 3, '0')  
WHERE CLAVE_CAT IS NULL;

-- geo data
UPDATE APP1.CAT_PREDIO SET CLAVE_CAT = '0205' || LPAD(parroquia::TEXT, 2, '0') || LPAD(zona::TEXT, 2, '0') || LPAD(sector::TEXT, 2, '0') || LPAD(mz::TEXT, 3, '0') || LPAD(solar::TEXT, 3, '0') || LPAD(bloque::TEXT, 2, '0') || LPAD(piso::TEXT, 3, '0') || LPAD(unidad::TEXT, 3, '0')  
WHERE CLAVE_CAT ILIKE '%020550';




-- descomponer clave catastral

CREATE OR REPLACE FUNCTION sgm_censocat.descomponer_clave(clave_cat character varying)
  RETURNS SETOF record AS
$BODY$
BEGIN
		IF (TRIM(CLAVE_CAT) IS NULL AND TRIM(CLAVE_CAT) = '') THEN 
    	RETURN;
    END IF;
    CLAVE_CAT = TRIM(CLAVE_CAT);
	IF(LENGTH(CLAVE_CAT) = 24) THEN 
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
    											'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
    											'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' || 
                          'SUBSTRING('''||CLAVE_CAT||''', 11, 3)::SMALLINT AS MZ, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 14, 3)::SMALLINT AS LOTE, ' || 
    											'SUBSTRING('''||CLAVE_CAT||''', 17, 3)::SMALLINT AS BLOQUE, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 20, 2)::SMALLINT AS PISO, ' || 
                          'SUBSTRING('''||CLAVE_CAT||''', 22, 3)::SMALLINT AS UNIDAD' ;
         ELSE
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
    			  'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
    			  'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' || 
                          'SUBSTRING('''||CLAVE_CAT||''', 11, 3)::SMALLINT AS MZ, ' ||
                          'SUBSTRING('''||CLAVE_CAT||''', 14, 3)::SMALLINT AS LOTE ' ;
         END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION sgm_censocat.descomponer_clave(character varying)
  OWNER TO sisapp;


-- Function: sgm_censocat.descomponer_clave_ant(character varying)

-- DROP FUNCTION sgm_censocat.descomponer_clave_ant(character varying);

CREATE OR REPLACE FUNCTION sgm_censocat.descomponer_clave_ant(clave_cat character varying)
  RETURNS SETOF record AS
$BODY$
BEGIN
		IF (TRIM(CLAVE_CAT) IS NULL AND TRIM(CLAVE_CAT) = '') THEN 
    	RETURN;
    END IF;
	CLAVE_CAT = TRIM(CLAVE_CAT);
    IF LENGTH(clave_cat)=9 THEN
       RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 1)::SMALLINT AS SECTOR ';
    ELSIF LENGTH(clave_cat)=10 THEN
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR ';
    ELSIF LENGTH(clave_cat)=11 THEN
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 10, 1)::SMALLINT AS MZ ';
    ELSIF LENGTH(clave_cat)=16 THEN
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 11, 2)::SMALLINT AS MZ, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 13, 3)::SMALLINT AS LOTE, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 15, 1)::SMALLINT AS BLOQUE ';
    ELSIF LENGTH(clave_cat)=17 THEN
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 11, 2)::SMALLINT AS MZ, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 13, 3)::SMALLINT AS LOTE, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 15, 2)::SMALLINT AS BLOQUE ';
	ELSIF LENGTH(clave_cat)=19 THEN
		RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 11, 2)::SMALLINT AS MZ, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 13, 3)::SMALLINT AS LOTE, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 15, 2)::SMALLINT AS BLOQUE ';
    ELSE
       RETURN QUERY EXECUTE 'SELECT CAST(SUBSTRING('''||CLAVE_CAT||''', 1, 2) AS SMALLINT) AS PROVINCIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 3, 2)::SMALLINT AS CANTON, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 5, 2)::SMALLINT AS PARROQUIA, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 7, 2)::SMALLINT AS ZONA, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 9, 2)::SMALLINT AS SECTOR, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 11, 2)::SMALLINT AS MZ, ' ||
					'SUBSTRING('''||CLAVE_CAT||''', 13, 3)::SMALLINT AS LOTE, ' || 
					'SUBSTRING('''||CLAVE_CAT||''', 16, 3)::SMALLINT AS BLOQUE ';
    END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION sgm_censocat.descomponer_clave_ant(character varying)
  OWNER TO sisapp;
  
  



