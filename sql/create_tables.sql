CREATE TABLE public.roles (
    role VARCHAR(20) PRIMARY KEY
);

CREATE TABLE public.workspace_types (
    type VARCHAR(50) PRIMARY KEY
);

CREATE TABLE public.users (
    login VARCHAR(50) PRIMARY KEY,
    role VARCHAR(20) NOT NULL REFERENCES public.roles(role)
);

CREATE TABLE public.workspaces (
    id UUID PRIMARY KEY,
    type VARCHAR(50) NOT NULL REFERENCES public.workspace_types(type),
    price NUMERIC(10,2) NOT NULL,
    available BOOLEAN NOT NULL
);

CREATE TABLE public.reservations (
    id UUID PRIMARY KEY,
    user_login VARCHAR(50) NOT NULL,
    workspace_id UUID NOT NULL,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_login) REFERENCES public.users(login) ON DELETE CASCADE,
    CONSTRAINT fk_workspace FOREIGN KEY (workspace_id) REFERENCES public.workspaces(id) ON DELETE CASCADE
);