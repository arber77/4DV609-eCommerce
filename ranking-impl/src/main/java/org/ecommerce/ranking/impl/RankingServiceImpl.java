package org.ecommerce.ranking.impl;

import org.ecommerce.ranking.api.Ranking;
import org.ecommerce.ranking.api.CreateRankingRequest;
import org.ecommerce.ranking.api.CreateRankingResponse;
import javax.inject.Inject;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import akka.Done;
import akka.NotUsed;

import org.ecommerce.ranking.api.AbstractRanking;
import org.ecommerce.ranking.api.RankingService;

public class RankingServiceImpl implements RankingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RankingServiceImpl.class);

	private final PersistentEntityRegistry persistentEntities;
	private final CassandraSession db;

	@Inject
	public RankingServiceImpl(PersistentEntityRegistry persistentEntities, CassandraReadSide readSide,
			CassandraSession db) {
		this.persistentEntities = persistentEntities;
		this.db = db;

		persistentEntities.register(RankingEntity.class);

	}

	@Override
	public ServiceCall<NotUsed, Ranking> getRanking(String rankingId) {
		return (req) -> {
			return persistentEntities.refFor(RankingEntity.class, rankingId).ask(GetRanking.of()).thenApply(reply -> {
				LOGGER.info(String.format("The seller was rated", rankingId));
				if (reply.getRanking().isPresent())
					return reply.getRanking().get();
				else
					throw new NotFound(String.format("The rating was not received", rankingId));
			});
		};
	}

	@Override
	public ServiceCall<CreateRankingRequest, Done> setRanking(String rankingId) {
		return request -> {
			LOGGER.info("Rating a seller: ", request);
			//UUID uuid = UUID.fromString(rankingId);
			//BigDecimal bigdecimal = BigDecimal.valueOf(Long.parseLong(request));
			return persistentEntities.refFor(RankingEntity.class, rankingId).ask(ChangeRanking.of(request.getRating()));
		};
	}

	@Override
	public ServiceCall<CreateRankingRequest, CreateRankingResponse> createRanking() {
		return request -> {
			LOGGER.info("Rating a seller: ", request);
			UUID uuid = UUID.randomUUID();
			return persistentEntities.refFor(RankingEntity.class, uuid.toString()).ask(CreateRanking.of(request));
		};
	}
}
