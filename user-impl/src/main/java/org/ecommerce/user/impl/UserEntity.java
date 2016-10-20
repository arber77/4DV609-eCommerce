package org.ecommerce.user.impl;

import org.ecommerce.user.api.CreateUserResponse;
import org.ecommerce.user.api.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEntity.class);

    @Override
    public Behavior initialBehavior(Optional<UserState> snapshotState) {
        LOGGER.info("Setting up initialBehaviour with snapshotState = {}", snapshotState);
        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(
                UserState.of(Optional.empty(), LocalDateTime.now()))
        );

        // Register command handler
        b.setCommandHandler(CreateUser.class, (cmd, ctx) -> {
            if (state().getUser().isPresent()) {
                ctx.invalidCommand(String.format("User %s is already created, UserId Should Be Unique", entityId()));
                return ctx.done();
            } else {
                User user = User.of(UUID.fromString(entityId()), cmd.getCreateUserRequest().getUUID(),
                        cmd.getCreateUserRequest().getPassword());
                final UserCreated userCreated = UserCreated.builder().user(user).build();
                LOGGER.info("Processed CreateUser command into UserCreated event {}", userCreated);
                return ctx.thenPersist(userCreated, evt ->
                        ctx.reply(CreateUserResponse.of(userCreated.getUser().getUUID(),userCreated.getUser().getUserId())));
            }
        });

        // Register event handler
        b.setEventHandler(UserCreated.class, evt -> {
                    LOGGER.info("Processed UserCreated event, updated user state");
                    return state().withUser(evt.getUser())
                            .withTimestamp(LocalDateTime.now());
                }
        );

        // Register read-only handler eg a handler that doesn't result in events being created
        b.setReadOnlyCommandHandler(GetUser.class,
                (cmd, ctx) -> {
                    LOGGER.info("Processed GetUser command, returned user");
                    ctx.reply(GetUserReply.of(state().getUser()));
                }
        );

        return b.build();
    }
}