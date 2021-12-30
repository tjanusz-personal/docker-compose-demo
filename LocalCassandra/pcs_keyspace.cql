-- keyspace pcs
CREATE KEYSPACE pcs WITH durable_writes = true AND replication = {'class' : 'SimpleStrategy','replication_factor' : 1};

-- pcdmpg table create and solr index 
CREATE TABLE pcs.pcdmpg (id uuid, cdb text,mktnm text,pcds text,ptyp text,ptyptc text,solr_query text,PRIMARY KEY (id));
CREATE SEARCH INDEX ON pcs.pcdmpg;

-- insert rows into table pcdmpg 
INSERT INTO pcs.pcdmpg(id,cdb,mktnm,pcds,ptyp,ptyptc) values (UUID(), 'TC9','mktnm_value','pcds_value','ptyp_value','ptyptc_value');
INSERT INTO pcs.pcdmpg(id,cdb,mktnm,pcds,ptyp,ptyptc) values (UUID(), 'TC10','mktnm_value','pcds_value','ptyp_value','ptyptc_value');

-- create illustration table
CREATE TABLE pcs.illustration (id uuid,pcd text,run boolean,PRIMARY KEY (id));

-- add dummy row into illustration 
INSERT INTO pcs.illustration(id,pcd,run) values (7ef202d1-f26b-46c2-95ca-b57e548bcd6a,'test1',true);
INSERT INTO pcs.illustration(id,pcd,run) values (UUID(),'test1',true);

