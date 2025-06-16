package exception;

import java.util.UUID;

public class WorkspaceNotFoundException extends RuntimeException {
    public WorkspaceNotFoundException(UUID id) {
        super("Workspace with ID " + id + " not found");
    }
}
