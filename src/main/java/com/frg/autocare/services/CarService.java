/**
 * AutoCare REST API - Car service component.
 * Copyright (C) 2024  AutoCare REST API original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this application.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.frg.autocare.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frg.autocare.entities.Car;
import com.frg.autocare.entities.Tool;
import com.frg.autocare.repository.CarRepository;
import com.frg.autocare.repository.ToolRepository;
import com.frg.autocare.technical.ModelObject;
import com.frg.autocare.technical.ModelObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

  @Autowired private CarRepository carRepository;

  @Autowired private ToolRepository toolRepository;

  private final ObjectMapper mapper = new ObjectMapper();

  public String getAll() {
    List<Car> cars = carRepository.findAll();
    List<ModelObject> carList = new ArrayList<>();

    for (Car car : cars) {
      ModelObjectBuilder carBuilder =
          ModelObjectBuilder.createBuilder()
              .addAttribute("id", car.getId())
              .addAttribute("model", car.getModel())
              .addAttribute("make", car.getMake())
              .addAttribute("clientName", car.getClient().getName())
              .addAttribute("maintainerName", car.getMaintainer().getName());

      List<Tool> tools = toolRepository.findToolsByMaintainerId(car.getMaintainer().getId());
      ModelObject[] toolBuilders =
          tools.stream()
              .map(
                  tool ->
                      ModelObjectBuilder.createBuilder()
                          .addAttribute("id", tool.getId())
                          .addAttribute("name", tool.getName())
                          .build())
              .toArray(ModelObject[]::new);

      carBuilder.addArrayAttribute("tools", toolBuilders);

      ModelObject finalBuilder =
          ModelObjectBuilder.createBuilder().addAttribute("car", carBuilder.build()).build();

      carList.add(finalBuilder);
    }

    try {
      return this.mapper.writeValueAsString(carList);
    } catch (Exception e) {
      throw new RuntimeException("Error converting to JSON", e);
    }
  }
}
