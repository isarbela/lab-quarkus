package infrastructure.repositories;

import domain.Candidate;
import domain.Election;
import domain.ElectionRepository;
import io.quarkus.cache.CacheResult;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.sortedset.SortedSetCommands;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RedisElectionRepository implements ElectionRepository {

    private static final Logger LOGGER = Logger.getLogger(RedisElectionRepository.class);
    private static final String KEY = "election:";
    private final SortedSetCommands<String, String> sortedSetCommands;
    private final KeyCommands<String> keyCommands;

    public RedisElectionRepository(RedisDataSource dataSource) {
        this.sortedSetCommands = dataSource.sortedSet(String.class, String.class);
        this.keyCommands = dataSource.key(String.class);
    }


    @Override
    public List<Election> findAll() {
        LOGGER.info("Retrieving elections from redis");
        return keyCommands
                .keys(KEY + "*")
                .stream()
                .map(id -> findById(id.replace(KEY, "")))
                .toList();
    }

    @Override
    @CacheResult(cacheName = "memoization")
    public Election findById(String id) {
        LOGGER.info("Retrieving election " + id + " from redis");
        return new Election(id, sortedSetCommands
                .zrange(KEY + id, 0, -1)
                .stream()
                .map(Candidate::new)
                .toList());
    }

    @Override
    public void vote(String id, Candidate candidate) {
        LOGGER.info("Voting for " + candidate.id());
        sortedSetCommands.zincrby(KEY + id, 1, candidate.id());
    }
}
