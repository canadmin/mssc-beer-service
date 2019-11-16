package can.springframework.msscbeerservice.web.controller;

import can.springframework.msscbeerservice.services.BeerService;
import can.springframework.msscbeerservice.web.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/beer")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;

    @GetMapping({"/{beerId}"})
    public ResponseEntity<BeerDto> getBeerById(@PathVariable UUID beerId){
        return new ResponseEntity<BeerDto>(beerService.getById(beerId),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity saveNewBeer(@RequestBody @Validated BeerDto beerDto){
        return new ResponseEntity(beerService.saveNewBeer(beerDto),HttpStatus.CREATED);

    }

    @PutMapping("/{beerId}")
    public ResponseEntity updateBeerById(@PathVariable UUID beerId,@RequestBody @Validated BeerDto beerDto){
        return new ResponseEntity(beerService.updateBeer(beerId,beerDto),HttpStatus.OK);
    }

    @DeleteMapping("/{beerId}")
    public void deleteBeerById(@PathVariable UUID beerId){

    }
}
