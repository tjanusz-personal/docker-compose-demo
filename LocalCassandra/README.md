# Local Developer Cassandra Setup via Docker Image

This file outlines some of the steps and gotcha's found when configuring cassandra to run locally on a developer machine.

To get the latest cassandra docker image follow the basic steps in this article (Steps 1 - 4)
https://medium.com/@michaeljpr/five-minute-guide-getting-started-with-cassandra-on-docker-4ef69c710d84

Notes:
* **There are multiple versions of data stax images!. The POC quickly tested 'latest' (6.x) and the 5.1.16 version (docker pull datastax/dse-server:5.1.16 - matches current prod) only.**
* **Devs should be cognizant of which version being loaded locally.**
* **THE POC WAS LIMITED TO BASIC STARTUP AND SIMPLE TABLES SO FURTHER ANALYSIS IS REQUIRED TO VALIDATE OTHER DEVELOPMENT USE CASES**
* **After loading all tables/data there may be issues re-starting/re-using the same container (e.g. commit log replaying). This is probably due to not fully mapping local volumes to cassandra configuration directories**.

Once you have a the image installed locally here's some details on how to configure to run locally.

## Creating the DSE container for running
Creating the container requires some basic port mapping and data center settings.

There is an example docker-compose-dse.yml file in this directory with all default configuration.

Launching the container is done either command line docker run/restart **OR** docker-compose commands

```
# Docker compose launch (use docker-compose-dse.yml file) (run in detached mode)
# must use docker logs my-dse to monitor status of startup
docker-compose -f docker-compose-dse.yml up --detach

# Docker compose launch (use docker-compose-dse.yml file) run inside current terminal window
docker-compose -f docker-compose-dse.yml up

# OR

# create the container (expose 9042 port on your local machine IP) ('Search' datacenter) 
# name container 'my-dse' and include DSE 'search' aka Solr
docker run -p 127.0.0.1:9042:9042 -e DS_LICENSE=accept -e DC=Search --name my-dse -d datastax/dse-server -s
```

Once the container starts it takes approx 1 minute for DSE to fully start. You can easily view the status by inspecting the logs from within the container.
```
# Look at DSE logs in container
docker logs my-dse

# keep refreshing until you see a DSE started log message.
# DseDaemon.java:788 - DSE startup complete.
```

If receive errors when restarting the local cluster refer to the logs to see what the log error is. The POC typically just removed the container after usage and manually rebuilt it during each usage to ensure a clean startup.


## Loading Keyspace and Table Defs
Once the server is started we need to add the keyspaces and table definitions BEFORE running the POC tests. The POC code references the PCS keyspace with the Illustration and Pcdmpg tables.

**Note: the example docker container described does NOT mount an external drive so any Cassandra keyspaces and table definitions created within the container are destroyed once the container is removed (e.g. docker container prune).**

All sample CQL statements can be found in the pcs_keyspace.cql file. These statements were obtained by running a manual export of the pcs keyspace using devCenter and slightly modifying the CQL.

With the container running, start a cqlsh session within a terminal window to manually run the cql commands to create the kespace and tables.

```
# start cqlsh w/in the my-dse container
docker exec -it my-dse cqlsh

# at cqlsh prompt (manually run each line from pcs_keyspace.cql file)
cqlsh> CREATE KEYSPACE pcs WITH durable_writes = true AND replication = {'class' : 'SimpleStrategy','replication_factor' : 1};

# Repeat for each command.
# Note: it is assumed possible to take the entire file and copy it into the container and run the whole file at once. The POC did NOT attempt this though.
```

Notes:
* Keyspace was created with a simple strategy and replication factor 1 ('class' : 'SimpleStrategy','replication_factor' : 1)
* All tables were created using simple defaults
* Search indexes were created using (CREATE SEARCH INDEX ON pcs.pcdmpg;) statements

```
# Select all tables from pcs keyspace
SELECT * FROM system_schema.tables WHERE keyspace_name = 'pcs';

# select from pcs illustration
select * from pcs.illustration;
```

## Spring Profile Modifications
With a local Cassandra running, we can modify our local or test spring profile to refer to this as our datastore (instead of the current DEV DBs).

In the application.properties (or application-local.properties) modify the cassandra.pcs properties connection points to point to the local instance

```
# local cassandra.pcs property modified to point to 127.0.0.1 (or whatever IP you mapped your container to use)
# refer to "docker run -p 127.0.0.1:9042:9042 -e DS_LICENSE=accept -e DC=Search --name my-dse -d datastax/dse-server -s"
# POC code assumes PCS keyspace and 9042 port are defaults.
# There is no authentication enabled so dummy user/password are fine
cassandra.pcs={\"USER_NAME\":\"someUser\",\"USER_PASSWORD\":\"somePass\",\"CONTACT_POINTS\":\"127.0.0.1\"}

# enable spring cassandra connection (tells the POC spring code to try and load all Cassandra repostority beans/config/etc.)
spring.data.cassandra.present=true
```

The POC code has a CassandraServiceTests.java file with two tests disabled which should be enabled to verify DB connections.

```
# Run the Cassandra Service Tests to verify connector works.
# If DB configured correctly with sample test data this test should pass.
mvn -Dtest=CassandraServiceTests test
```

## DataStax Studio Execution
Datastax studio can also be run using docker. The POC utilized Datastax studio to connect to the existing dev center cluster (instead of having to rely upon DevCenter fat client UI. - Note: DevCenter is now deprecated!)

There is an example docker-compose-dse-studio.yml file in this directory with all default configuration.

Launching the container is done either command line docker run/restart **OR** docker-compose commands

```
# Docker compose launch (use docker-compose-dse-studio.yml file) (run in detached mode)
# must use docker logs my-dse-studio to monitor status of startup
docker-compose -f docker-compose-dse-studio.yml up --detach

# Docker compose launch (use docker-compose-dse.yml file) run inside current terminal window
docker-compose -f docker-compose-dse-studio.yml up

# create the container (expose 9091 port on your local machine IP)
# name container 'my-dse-studio'
docker run -e DS_LICENSE=accept -p 127.0.0.1:9091:9091 --memory 1g --name my-studio -d datastax/dse-studio
```

**Note: the example docker container described does NOT mount an external drive so any configuration/workspaces created within the container are destroyed once the container is removed (e.g. docker container prune).**

Once datastax studio container is up and running navigate the brower to http://127.0.0.1:9091/ to launch the studio.

### Configure Dev Cluster Connection
In the top left corner menu (Main Menu) choose "Connections" to launch the connections UI. Click the plus button to add a new connection. Enter valid values for each of the required fields. NOTE: Host/IP accepts valid comma separated host names (ddse101.mycompany.com,ddse102.mycompany.com,ddse103.mycompany.com,ddse104.mycompany.com). 

Once the connection is established you're ready to start creating notebooks to run queries, etc. Just make sure to select this new connection when creating the new notebook.

Misc Notes:
* Dev runs an older version of DSE 5.1.x which appears to be compatible with the latest version of the studio (although some warnings/links state possible incompatibility issues)
* You can export notebooks by clicking inside the results pane and pressing "E" to launch the export wizard. The POC did NOT validate export/import. Please refer to the studio help (top right corner of UI) for a list of all commands.


## Misc docker commands
Common docker commands to run to interact with the container.

```
# Stop container
docker stop my-dse

# restart container
docker restart my-dse

# start cqlsh w/in the my-dse container
docker exec -it my-dse cqlsh

# determine ip address of container
docker exec -it my-dse hostname -i

# inspect contents of container
docker inspect my-dse

# Connect to running container bash shell
docker exec -it my-dse bash

# show me running processes named my-dse and only Id, State, Name
docker ps --format "{{.ID}}: {{.State}} {{.Names}}" --filter name=my-dse

```

## Cqlsh commands
Common cql commands to run within a connected shell.

```
# see what keyspaces are there?
SELECT * FROM system_schema.keyspaces;

# see what datacenter i'm running in
use system;
select data_center from local;

# see what tables are in pcs keyspace
SELECT * FROM system_schema.tables WHERE keyspace_name = 'pcs';
SELECT * FROM system_schema.columns WHERE keyspace_name = 'keyspace_name' AND table_name = 'table_name';

# Dump all definitions for given keyspace
DESCRIBE KEYSPACE pcs;

# describe specific table
DESCRIBE TABLE pcs.fndmpg;

```

## More Information

* [Good intro to cassandra docker](https://medium.com/@michaeljpr/five-minute-guide-getting-started-with-cassandra-on-docker-4ef69c710d84)
* [datastax docker image docs](https://github.com/datastax/docker-images)
* [Docker hub - DSE](https://hub.docker.com/r/datastax/dse-server)
* [Docker hub - DSE Studio](https://hub.docker.com/r/datastax/dse-studio)