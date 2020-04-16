package com.smartPark.spotPlacement.service;

import com.smartPark.spotPlacement.SpotPlacementApplication;
import com.smartPark.spotPlacement.model.*;
import com.smartPark.spotPlacement.repository.*;
import org.apache.el.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.smartPark.spotPlacement.model.OccupancyStatus;

import static com.smartPark.spotPlacement.repository.OccupancyStatusRepository.*;

@Service
public class SpotPlacementService {

    @Autowired
    private MapRepository mapRepo;
    @Autowired
    private SpotAvailabilityRepository spotAvailRepo;

    @Autowired
    private CamToGlobalIdRepository camToGlobalIdRepo;

    @Autowired
    private CamStatusRepository camStatusRepo;

    @Autowired
    private OccupancyStatusRepository occupancystatusrepository;

    @Autowired
    private CamerasRepository camerasRepo;

    public Object getSpotsGpsCordinates() {
        System.out.println("It ran");
        List<Map> mapList = mapRepo.findAll();

        SpotsGPSCordinates[] gpsCordinatesList = new SpotsGPSCordinates[mapList.size()];
        int size = mapList.size();
        for (int j = size; j > 0; j--) {
            int currentIndex = size - j;
            Map mapObj = mapList.get(currentIndex);
            Object gpsCordinates = mapObj.getGps_points();
            SpotsGPSCordinates tempObj = new SpotsGPSCordinates(gpsCordinates);
            gpsCordinatesList[currentIndex] = tempObj;
        }
        return gpsCordinatesList;
    }

//    public List<SpotsGPSCordinates> getSpotStatus() {
//
//        List<SpotsGPSCordinates> spotGPSCordsArr = new ArrayList<>();
//
//        // TODO : get all spot gps co-ordinates w.r.t global id
//        List<Map> mapList = mapRepo.findAll();
//
//        // TODO : get all the spots who are occupied w.r.t global id
//        List<SpotAvailability> spotAvailList = spotAvailRepo.findAll();
//
//        // TODO : loop through each other, create an array of SpotGPSCordinates which are occupied and return
//
//        for (int i = 0; i < spotAvailList.size(); i++) {
//            SpotAvailability spotAvailRecord = spotAvailList.get(i);
//            int globalId = spotAvailRecord.getId();
//            String spotStatus = spotAvailRecord.getStatus();
//            if (spotStatus.equals("closed")) {
//                for (int j = 0; j < mapList.size(); j++) {
//                    Map mapRecord = mapList.get(j);
//                    if (mapRecord.getId() == globalId) {
//                        // TODO : fetch GPS cordinates
//                        Object gpsPoints = mapRecord.getGps_points();
//                        // TODO : Create spot records
//                        SpotsGPSCordinates spotsGPSCord = new SpotsGPSCordinates(gpsPoints);
//                        spotGPSCordsArr.add(spotsGPSCord);
//                    }
//                }
//            }
//        }
//        return spotGPSCordsArr;
//    }

    // TODO : Replace request body type object to somethimg else
    public List<SpotAvailability> updateSpotStatus(ArrayList<SpotUpdateRequest> spotUpdateStatusBody) {
        int spotStatusSize = spotUpdateStatusBody.size();

        List<SpotAvailability> spotAvailUpdatedList = new ArrayList<>();
        for (int i = 0; i < spotStatusSize; i++) {
            SpotUpdateRequest SpotsStatusObj = spotUpdateStatusBody.get(i);
            SpotAvailability spotAvailUpdatedObj = this.updateSpots(SpotsStatusObj);
            spotAvailUpdatedList.add(spotAvailUpdatedObj);
        }

        return spotAvailUpdatedList;

        //[
// { “cam_id”: “GL213Q”,
// “space_updates”: [
//   { “id”: “L1S15”, “status”: “open” },
//   { “id”: “L5S01”, “status”: “open” },
//   { “id”: “L6S10”, “status”: “closed” },
//   { “id”: “L7S03”, “status”: “closed” }
// ]
// },
//
//    {“cam_id”: “GL223A”,
        //  “space_updates”: [
        //   { “id”: “L1S15”, “status”: “open” },
        //   { “id”: “L5S01”, “status”: “open” },
        //   { “id”: “L6S10”, “status”: “closed” },
        //   { “id”: “L7S03”, “status”: “closed” }
        // ]
        // }
        // ....
        // ]
        // TODO : Change later

        // TODO : loop through the body for multiple cam spot status
    }

    public SpotAvailability updateSpots (SpotUpdateRequest spotUpdateObj){
        int count = 0;
        String cameraId = spotUpdateObj.getCamId();
        ArrayList<SpotUpdateStatusRequest> spaceUpdates = spotUpdateObj.getSpaceUpdates();

        SpotAvailability res = null;
        int spaceUpdatesLength = spaceUpdates.size();
        // Loop through all the space updates
        for(int i = 0; i < spaceUpdatesLength; i++){
            SpotUpdateStatusRequest spotStatusObj =  spaceUpdates.get(i);
            String cameraSpotId = spotStatusObj.getId();
            String spotStatusString = spotStatusObj.getStatus();
            String camSpotId  = cameraId + cameraSpotId;
            long currentUnixTime = System.currentTimeMillis() / 1000L;
            // TODO : Spot detail is used in updating the spot avaiability record
            Map spotDetail = this.fetchSpotDetailsByGlobalId(camSpotId);

            int globalId = spotDetail.getId();
            // TODO : Check if spot availability record is present or not in collection with particular global id

            SpotAvailability spotRecord = this.fetchSpotAvailabilityRecord(globalId);
            if(spotRecord != null){
                // TODO : Update spot record
                ArrayList<SpotStatus> camArr= spotRecord.getCam_reports();
                for (int arrSize =0; arrSize<camArr.size();arrSize++){
                    String camId = camArr.get(arrSize).getId();
                    CamStatus camStatusRecord = this.fetchCamStatus(camId);
                    Cameras cameraObj = this.fetchCamerasRecord(camId);
                     res = this.updateSpotAvailability(globalId,spotStatusString);
                }
            }else {
                // TODO : Create a spot record
                SpotStatus spotStatusObject = new SpotStatus(cameraSpotId,spotStatusString);
                ArrayList<SpotStatus> spotStatusList = new ArrayList<>();
                spotStatusList.add(spotStatusObject);
                SpotAvailability newSpotAvailObj = new SpotAvailability(globalId,spotStatusString,spotStatusList,currentUnixTime);
                res = spotAvailRepo.save(newSpotAvailObj);

            }
            //System.out.println("Occupance status");
            //OccupancyStatus newOccupancyStatus  =  new OccupancyStatus(globalId+1,cameraId+1, spotStatusString,"",currentUnixTime);
           //System.out.println(spotStatusString);
            //String s2="Open";
           // if(spotStatusString.compareTo(s2) == 0){
             //  count = count+1;
             //  System.out.println(count);
           // }

            //occupancystatusrepository.save(newOccupancyStatus);
            //System.out.println(newOccupancyStatus);
            //System.out.println(newOccupancyStatus);

        }
        //System.out.println(res);
        return res;
    }

    public SpotAvailability updateSpotAvailability(int globalId, String spotStatus){

        List<SpotAvailability> spotAvailList = spotAvailRepo.findAll();

        SpotAvailability spotAvailObj = null;
        for(int i=0 ; i < spotAvailList.size();i++){
            int spotAvailId = spotAvailList.get(i).getId();
            if(globalId == spotAvailId){
                spotAvailObj = spotAvailList.get(i);
                break;
            }
        }

        assert spotAvailObj != null;
        spotAvailObj.setDate(System.currentTimeMillis() / 1000L);
        spotAvailObj.setStatus(spotStatus);
        spotAvailObj = spotAvailRepo.save(spotAvailObj);
        return spotAvailObj;
    }

    public CamStatus fetchCamStatus(String camId){
        List<CamStatus> camStatusList =  camStatusRepo.findAll();
        CamStatus finalCamStatus = null;
        for(int i=0;i<camStatusList.size();i++){
            String camStatusId = camStatusList.get(i).getId();
            if(camStatusId == camId){
                finalCamStatus = camStatusList.get(i);
                break;
            }
        }
        return finalCamStatus;
    }

    public Cameras fetchCamerasRecord(String camId){
        List<Cameras> camerasList =  camerasRepo.findAll();
        Cameras finalCamerasRec = null;
        for(int i=0;i < camerasList.size(); i++){
            String cameraId = camerasList.get(i).getId();
            if(cameraId.equals(camId)){
                finalCamerasRec = camerasList.get(i);
                break;
            }
        }
        return finalCamerasRec;
    }

    public SpotAvailability fetchSpotAvailabilityRecord(int globalId){

        List<SpotAvailability> spotAvailList = spotAvailRepo.findAll();
        SpotAvailability finalSpotAvailRecord = null;
        for (int i=0; i<spotAvailList.size();i++){
            int spotAvailId = spotAvailList.get(i).getId();
            if(spotAvailId == globalId){
                finalSpotAvailRecord = spotAvailList.get(i);
            }
        }
        return finalSpotAvailRecord;
    }

    public Map fetchSpotDetailsByGlobalId(String camSpotId){
        // Get global id with
        List<CamToGlobalId> camToGlobalIdList = camToGlobalIdRepo.findAll();

        CamToGlobalId CamToGlobalIdRec = null;
        int globalId =0;

        for(int i =0; i < camToGlobalIdList.size(); i++){
            String currentId = camToGlobalIdList.get(i).getId();
            if(currentId.equals(camSpotId)){
                globalId = camToGlobalIdList.get(i).getGlobalId();
                break;
            }
        }

        Map finalMapObj = null;
        if(globalId > 0){
            List<Map> mapList = mapRepo.findAll();
            for(int j = 0; j < mapList.size(); j++){
                Map mapObj = mapList.get(j);
                int mapId = mapObj.getId();
                if(mapId == globalId){
                    finalMapObj = mapObj;
                    break;
                }
            }
        }
        return finalMapObj;
    }

    public List<Map> getSpotDetails(){
        List<Map> mapList =  mapRepo.findAll();
        return mapList;
    }

    public List<SpotAvailability> getSpotStatus(){
        List<SpotAvailability> spotStatusList =  spotAvailRepo.findAll();
        return spotStatusList;
    }

//    public Object addCamera(Object cameraSpotDetails){
//
//        // TODO : Implement request model cameraSpotDetails
//        Integer cameraId = cameraSpotDetails.cam_id;
//
//        // TODO : Separate Function, Fetch the camera details from camera table.
//
//        Object camDetails = getCamDetails();
//
//        //  Two scenarios is possible, camera record is present and not present.
//
//        // TODO : First Scenario , camera record not present ,
//        //  request body will have cam_id, cam_name, cam_num_of_spaces, cam_timeout
//
//        // TODO : Second scenario is camera record already exist, then it will throw internal server error
//
//        // TODO : Two scenarios, save camera details or else send failure message
//        //  with failure status
//
//        // TODO : Separate method for saving camera details into camera table
//
//        // TODO : Now Add or Update Spots
//
//        // TODO : Separate method for Add or Update Spots
//
//        // TODO : If not successful, send 500 internal server error message
//
//        // TODO :
//
//        // TODO : Return successful or failure message
//
//
//        return new Object();
//    }

    // TODO : Adding/Updating Spots
    // TODO : loop through all the spaces array
    // TODO : Comparing one space coordinates with other all other in map table

}
