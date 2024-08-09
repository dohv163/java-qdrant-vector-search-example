package vn.hti.sf.testsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vn.hti.sf.testsearch.repository.QdrantRepository;

@SpringBootApplication
public class TestSearchApplication implements CommandLineRunner {

    @Autowired
    private QdrantRepository qdrantRepository;

    public static void main(String[] args) {
        SpringApplication.run(TestSearchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

       // qdrantRepository.initDataImageVector();

    }
}
