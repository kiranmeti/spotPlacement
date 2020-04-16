package com.smartPark.spotPlacement.repository;

import com.smartPark.spotPlacement.model.OccupancyStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OccupancyStatusRepository extends MongoRepository<OccupancyStatus, String>   {
}
