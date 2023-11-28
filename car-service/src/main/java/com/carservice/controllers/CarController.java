package com.carservice.controllers;

import com.carservice.business.abstracts.CarService;
import com.carservice.dto.requests.CarImagesDto;
import com.carservice.dto.requests.CarInfoDto;
import com.carservice.entities.Car;
import com.carservice.entities.CarImages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final WebClient.Builder webClientBuilder;
    private final ImageController imageController;

    @GetMapping
    public List<Car> getAllCars() {
        List<Car> carInDb = carService.getAll();
        return carInDb;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable String id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/AddCar")
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        Car addCar = carService.add(car);
        return ResponseEntity.status(HttpStatus.OK).body(addCar);
    }

    @PostMapping("/AddCarImages")
    public String AddCarImages(@RequestBody List<CarImagesDto> carImagesDtos)  throws IOException {

        List<CarImages> carImagesList = new ArrayList<>();
        for(CarImagesDto item:carImagesDtos) {
            CarImages carImages = new CarImages();
            carImages.setId(item.getId());
            carImages.setCarId(item.getCarId());
            carImages.setCarImage(imageController.uploadImage(item.getBase64Data().replace(" ","")));
            carImagesList.add(carImages);
        }
        carService.addCarImages(carImagesList);
        return ("Araba görsel/görselleri eklendi.");
    }

    @GetMapping("getByCarId")
    public Optional<Car> getByCarId(@RequestParam String id) {
        return carService.getById(id);
    }

    @GetMapping("carStatus")
    public Boolean getByCarStatus(@RequestParam String id) {
       Optional<Car> car = carService.getById(id);
       return car.get().getCarStatus();
    }

    @PutMapping("carUpdate")
    public void updateCar(@RequestParam String id, @RequestBody Car car) {
        carService.updateCar(id, car);
    }

    /*@PostMapping
    public String submitOrder(@RequestBody CarInfoDto request) {
        // Web istekleri default async
        // sync
        return webClientBuilder.build()
                .get()
                .uri("http://rental-service/api/v1/rentals/car-status",
                        (uriBuilder) -> uriBuilder
                                .queryParam("carId", request.getId())
                                .queryParam("name", request.getName())
                                .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        // senkron
        // return new ResponseEntity<>(carStatus , carStatus ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }*/
}
