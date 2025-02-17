package can.springframework.msscbeerservice.services;

import can.springframework.msscbeerservice.domain.Beer;
import can.springframework.msscbeerservice.repositories.BeerRepository;
import can.springframework.msscbeerservice.web.controller.NotFoundException;
import can.springframework.msscbeerservice.web.mappers.BeerMapper;
import can.springframework.msscbeerservice.web.model.BeerDto;
import can.springframework.msscbeerservice.web.model.BeerPageList;
import can.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.springframework.cache.annotation.Cacheable;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;


    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false ") //showInventoryOnHand = false olduğunda cache mekanizması çalışır
    @Override
    public BeerPageList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {

        System.out.println("cağrıldı");
        BeerPageList beerPagedList;
        Page<Beer> beerPage;

        if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            //search both
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
            //search beer_service name
            beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
        } else if (StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            //search beer_service style
            beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        if (showInventoryOnHand){
            beerPagedList = new BeerPageList(beerPage
                    .getContent()
                    .stream()
                    .map(beerMapper::beerToBeerDtoWithInventory)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(beerPage.getPageable().getPageNumber(),
                                    beerPage.getPageable().getPageSize()),
                    beerPage.getTotalElements());
        } else {
            beerPagedList = new BeerPageList(beerPage
                    .getContent()
                    .stream()
                    .map(beerMapper::beerToBeerDto)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(beerPage.getPageable().getPageNumber(),
                                    beerPage.getPageable().getPageSize()),
                    beerPage.getTotalElements());
        }

        return beerPagedList;
    }
    @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false ") //showInventoryOnHand = false olduğunda cache mekanizması çalışır
    @Override
    public BeerDto getById(UUID beerId,Boolean showInventoryOnHand) {
        if(showInventoryOnHand) {
            return beerMapper.beerToBeerDtoWithInventory(
                    beerRepository.findById(beerId).orElseThrow(NotFoundException::new)
            );
        }else {
            return beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElseThrow(NotFoundException::new));
        }
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
    }

    @Override
    public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);

        beer.setBeerName(beerDto.getBeerName());
        beer.setBeerStyle(beerDto.getBeerStyle().name());
        beer.setPrice(beerDto.getPrice());
        beer.setUpc(beerDto.getUpc());
        return beerMapper.beerToBeerDto(beerRepository.save(beer));
    }



    @Cacheable(cacheNames = "beerUpcCache")
    @Override
    public BeerDto findByUpc(String upc) {
        return beerMapper.beerToBeerDtoWithInventory(beerRepository.findByUpc(upc));
    }
}
