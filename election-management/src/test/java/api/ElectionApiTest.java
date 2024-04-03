package api;

import domain.Candidate;
import domain.ElectionService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ElectionApiTest {

    @Inject
    ElectionApi api;

    @InjectMock
    ElectionService service;

    @Test
    void submit() {
        api.submit();

        verify(service).submit();
        verifyNoMoreInteractions(service);
    }
}