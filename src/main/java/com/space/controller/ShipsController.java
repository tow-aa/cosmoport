
package com.space.controller;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/rest/ships")
public class ShipsController {
    private ShipRepository shipRepository;
    private List<Ship> list = new ArrayList<>();

    @Autowired
    public ShipsController(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    //GetAll
    @GetMapping
    public List<Ship> getAll(@RequestParam Map<String, String> requestParam) {
        int pageSize = 3;
        int pageNumber = 0;
        String sortBy = "id";
        if (requestParam.containsKey("pageSize")) pageSize = Integer.valueOf(requestParam.get("pageSize"));
        if (requestParam.containsKey("pageNumber")) pageNumber = Integer.valueOf(requestParam.get("pageNumber"));
        if (requestParam.containsKey("order")) sortBy = ShipOrder.valueOf(requestParam.get("order")).getFieldName();
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortBy));
        list.clear();
        list.addAll(shipRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy, "id")));
        iterateList(requestParam, list);
        int start = (int) pageRequest.getOffset();
        int end = (start + pageRequest.getPageSize()) > list.size() ? list.size() : (start + pageRequest.getPageSize());
        Page<Ship> shipPage = new PageImpl<Ship>(list.subList(start, end), pageRequest, list.size());
        return shipPage.getContent();
    }

    //GetCount
    @GetMapping("count")
    public int getCount(@RequestParam Map<String, String> requestParam) {
        list.clear();
        list.addAll(shipRepository.findAll());
        return iterateList(requestParam, list).size();
    }

    //Фильтрация для GetAll и GetCount
    private List<Ship> iterateList(Map<String, String> requestParam, List<Ship> list) {
        Iterator<Ship> iterator = list.iterator();
        while (iterator.hasNext()) {
            Ship x = iterator.next();
            if (requestParam.containsKey("name")) if (!x.getName().contains(requestParam.get("name"))) {
                iterator.remove();
                continue;
            }
            if (requestParam.containsKey("planet")) if (!x.getPlanet().contains(requestParam.get("planet"))) {
                iterator.remove();
                continue;
            }
            if (requestParam.containsKey("shipType"))
                if (!(x.getShipType().name().equals(requestParam.get("shipType")))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("after"))
                if (x.getProdDate().getTime() <= Long.parseLong(requestParam.get("after"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("before"))
                if (x.getProdDate().getTime() >= Long.parseLong(requestParam.get("before"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("isUsed"))
                if (!x.getUsed().equals(Boolean.parseBoolean(requestParam.get("isUsed")))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("minSpeed"))
                if (x.getSpeed() <= Double.parseDouble(requestParam.get("minSpeed"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("maxSpeed"))
                if (x.getSpeed() >= Double.parseDouble(requestParam.get("maxSpeed"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("minCrewSize"))
                if (x.getCrewSize() <= Integer.parseInt(requestParam.get("minCrewSize"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("maxCrewSize"))
                if (x.getCrewSize() >= Integer.parseInt(requestParam.get("maxCrewSize"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("minRating"))
                if (x.getRating() <= Double.parseDouble(requestParam.get("minRating"))) {
                    iterator.remove();
                    continue;
                }
            if (requestParam.containsKey("maxRating"))
                if (x.getRating() >= Double.parseDouble(requestParam.get("maxRating"))) {
                    iterator.remove();
                }
        }
        return list;
    }

    //GetShip
    @GetMapping("{id}")
    public ResponseEntity<Ship> getShip(@PathVariable Long id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            return new ResponseEntity<Ship>(shipRepository.findById(id).get(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //DeleteShip
    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            Long idTest = Long.parseLong(String.valueOf(id));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (shipRepository.existsById(id)) {
            shipRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //CreateShip
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        try {
            if ((ship.getCrewSize() > 9999) || (ship.getCrewSize() < 0) || ship.getProdDate().getTime() < 0 || ship.getName().equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if (ship.getUsed() == null) ship.setUsed(false);
            ship.setRating();
            shipRepository.save(ship);
            return new ResponseEntity<Ship>(ship, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // UpdateShip
    @PostMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ship> update(@RequestBody Ship ship, @PathVariable Long id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!shipRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ship.setId(id);
        if (ship.getName() == null) ship.setName(shipRepository.findById(id).get().getName());
        if (ship.getSpeed() == null) ship.setSpeed(shipRepository.findById(id).get().getSpeed());
        if (ship.getPlanet() == null) ship.setPlanet(shipRepository.findById(id).get().getPlanet());
        if (ship.getProdDate() == null) ship.setProdDate(shipRepository.findById(id).get().getProdDate());
        if (ship.getShipType() == null) ship.setShipType(shipRepository.findById(id).get().getShipType());
        if (ship.getCrewSize() == null) ship.setCrewSize(shipRepository.findById(id).get().getCrewSize());
        if (ship.getUsed() == null) ship.setUsed(shipRepository.findById(id).get().getUsed());
        if ((ship.getCrewSize() > 9999) || (ship.getCrewSize() < 0) || ship.getProdDate().getTime() < 0 || ship.getName().equals(""))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ship.setRating();
        shipRepository.save(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }


}
