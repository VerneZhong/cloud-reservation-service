CREATE TABLE reservable_events (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    booking_opens_at TIMESTAMPTZ NOT NULL,
    booking_closes_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_event_booking_window
        CHECK (booking_opens_at < booking_closes_at),
    CONSTRAINT ck_event_booking_before_start
        CHECK (booking_closes_at <= starts_at),
    CONSTRAINT ck_event_status
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'CLOSED', 'CANCELLED'))
);

CREATE TABLE event_inventories (
    event_id UUID PRIMARY KEY
        REFERENCES reservable_events(id) ON DELETE CASCADE,
    capacity INTEGER NOT NULL,
    available INTEGER NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_inventory_capacity
        CHECK (capacity >= 0),
    CONSTRAINT ck_inventory_available
        CHECK (available >= 0 AND available <= capacity)
);

CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    event_id UUID NOT NULL
        REFERENCES reservable_events(id),
    quantity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    confirmed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_reservation_quantity
        CHECK (quantity > 0),
    CONSTRAINT ck_reservation_expiration
        CHECK (expires_at > created_at),
    CONSTRAINT ck_reservation_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'))
);

CREATE INDEX idx_reservations_user_created
    ON reservations(user_id, created_at DESC);

CREATE INDEX idx_reservations_event_status
    ON reservations(event_id, status);

CREATE INDEX idx_reservations_pending_expiration
    ON reservations(expires_at)
    WHERE status = 'PENDING';

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    user_id UUID NOT NULL,
    event_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_outbox_quantity
        CHECK (quantity > 0)
);

CREATE INDEX idx_outbox_unpublished_created
    ON outbox_events(created_at)
    WHERE published_at IS NULL;
