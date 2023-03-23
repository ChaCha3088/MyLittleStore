package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.dto.store.StoreTableCreationDto;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.storetable.StoreTableRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreTableService {

    private final StoreRepository storeRepository;

    private final StoreTableRepository storeTableRepository;

    @Transactional
    public Long createStoreTable(StoreTableCreationDto storeTableDto) throws NoSuchStoreException {
        //db에 가게가 없으면, 예외 발생
         Store findStore = findById(storeTableDto.getStoreId());

        //테이블 생성
        StoreTable createdStoreTable = findStore.createStoreTable();

        //저장
        Store savedStore = storeRepository.save(findStore);
        StoreTable savedStoreTable = storeTableRepository.save(createdStoreTable);

//        잔여 테이블이 있으면 테이블 생성 -> 이거는 테이블 사용 로직인 듯
//        if (tableNumbers - currentTableNumbers > 0) {
//            orderDto.createTable(new Order(orderDto));
//        } else {
//            throw new IllegalStateException("테이블이 가득 찼습니다.");
//        }

        return savedStoreTable.getId();
    }

    private Store findById(Long id) throws NoSuchStoreException {
        return storeRepository.findById(id).orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage()));
    }
}
