package can.springframework.msscbeerservice.bootstrap;

import can.springframework.msscbeerservice.domain.Beer;
import can.springframework.msscbeerservice.repositories.BeerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BeerLoader implements CommandLineRunner {
    private final BeerRepository beerRepository;

    public BeerLoader(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadBeerObjects();
    }

    private void loadBeerObjects() {
    if(beerRepository.count() == 0){
        beerRepository.save(Beer.builder().
                beerName("Tuborg")
                .beerStyle("IPA")
                .quantityToBrew(200)
                .upc(333745444545L)
                .price(new BigDecimal("12.95"))
                .minOnHand(12).build());
        beerRepository.save(Beer.builder().
                beerName("Efes")
                .beerStyle("PALE_ALE")
                .quantityToBrew(200)
                .upc(333745444235L)
                .price(new BigDecimal("11.95"))
                .minOnHand(12).build());
    }
        System.out.println("Loaders Beer : " + beerRepository.count());
    }

}
