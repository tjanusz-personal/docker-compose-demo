package com.mycompany.docker.demo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.mycompany.docker.demo.keyspace.pcs.Illustration;
import com.mycompany.docker.demo.keyspace.pcs.Pcdmpg;
import com.mycompany.docker.demo.models.AnimalResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@XRayEnabled
@RestController
public class EnvVarController {

    private static final Logger logger = LogManager.getLogger(EnvVarController.class);

    @Autowired
    private SampleService sampleService;

    @Autowired(required = false)
    private CassandraService cassandraService;

    @GetMapping("/heartbeat")
    String getHeartbeat() {
        return "alive";
    }

    @GetMapping("/env")
    Map<String, String> getEnvVars() {
        logger.info("Starting env");

        Map<String, String> envVars = System.getenv();

        logger.info(envVars);
        logger.info("Ending env");
        return System.getenv();
    }

    @GetMapping("/slowStuff")
    String getSlowStuff() {
        logger.info("Starting slowStuff");

        // dummy fibonacci sequence
        logger.debug("Going to create fibo value");
        int fiboValue = fibo(35);
        logger.debug("Done creating fiboValue");

        logger.info("Ending slow stuff with value");
        return String.format("slow stuff was: %s", fiboValue);
    }

    @GetMapping("/readSomethingFromVolume")
    String[] getReadSomethingFromVolume(@RequestParam(required = false) String pathName) throws IOException {
        logger.info("Starting readSomethingFromVolume");
        String folderName = "/home/app/data";
        if (pathName != null) {
            folderName = pathName;
        }
        File folder = new File(folderName);
        logger.info("folder exists: " + folder.exists());
        String[] allFiles = folder.list();
        logger.info("Ending readSomethingFromVolume");
        return allFiles;
    }

    @GetMapping("/writeSomethingToVolume")
    String getWriteSomethingToVolume(@RequestParam(required = false) String pathName) throws IOException {
        logger.info("Starting writeSomethingToVolume");
        String folderName = "/home/app/data";
        if (pathName != null) {
            folderName = pathName;
        }
        File folder = new File(folderName);
        if (!folder.isDirectory()) {
            logger.info("folder does not exist NOT writing");
            return "folder does not exist";
        }
        Random rand = new Random();
        String newFileName = "File" + rand.nextInt();
        File newFile = new File(folderName + "/" + newFileName);
        newFile.createNewFile();
        logger.info("Ending writeSomethingToVolume");
        return "wrote new file";
    }

    @GetMapping("/invokePublicApi")
    List<AnimalResponse> getInvokePublicApi(@RequestParam(required = false) String amount) {
        logger.info("invokePublicApi - Start");
        List<AnimalResponse> animals = sampleService.invokePublicApi(amount);
        logger.info("invokePublicApi - End");
        return animals;
    }


    @GetMapping("/invokeCassandra")
    Illustration getInvokeCassandra() {
        return cassandraService.doSomethingInCassandra(null);
    }

    @GetMapping("/invokeSolr")
    List<Pcdmpg> getInvokeSolr() {
        return cassandraService.doSomeSolrQuery();
    }


    int fibo(int n) {
        if (n < 2) {
            return 1;
        } else {
            return fibo(n - 2) + fibo(n - 1);
        }
    }

}