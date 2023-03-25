package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.dto.storetable.StoreTableFindDto;
import site.mylittlestore.dto.storetable.StoreTableFindDtoWithOrderFindDto;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreTableErrorMessage;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.exception.storetable.NoSuchStoreTableException;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.storetable.StoreTableRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreTableService {

    private final StoreRepository storeRepository;

    private final StoreTableRepository storeTableRepository;

    public StoreTableFindDto findStoreTableFindDtoById(Long storeTableId) throws NoSuchStoreTableException {
        StoreTable findStoreTable = storeTableRepository.findByIdWhereStoreTableStatusIsNotDeleted(storeTableId)
                .orElseThrow(() -> new NoSuchStoreTableException(StoreTableErrorMessage.NO_SUCH_STORE_TABLE.getMessage()));
        return findStoreTable.toStoreTableFindDto();
    }

    public StoreTableFindDtoWithOrderFindDto findStoreTableFindDtoWithOrderFindDtoByStoreId(Long storeTableId, Long storeId) {
        //가게에 속한 테이블만 찾아야지.
        StoreTable storeTableWithStoreAndOrderByIdAndStoreId = storeTableRepository.findStoreTableWithStoreAndOrderByIdAndStoreId(storeTableId, storeId)
                .orElseThrow(() -> new NoSuchStoreTableException(StoreTableErrorMessage.NO_SUCH_STORE_TABLE.getMessage()));

        //Dto로 변환
        return storeTableWithStoreAndOrderByIdAndStoreId.toStoreTableFindDtoWithOrderFindDto();
    }

    public List<StoreTableFindDto> findAllStoreTableFindDtoByStoreId(Long storeId) {
        //가게에 속한 테이블만 찾아야지.
        List<StoreTable> allStoreTableByStoreId = storeTableRepository.findAllStoreTableByStoreIdWhereStoreTableStatusIsNotDeleted(storeId);

        //Dto로 변환
        return allStoreTableByStoreId.stream()
                .map(m -> m.toStoreTableFindDto())
                .collect(Collectors.toList());
    }

    public List<StoreTableFindDtoWithOrderFindDto> findAllStoreTableFindDtoWithOrderFindDtoByStoreId(Long storeId) {
        //가게에 속한 테이블만 찾아야지.
        List<StoreTable> storeTableWithOrderByStoreId = storeTableRepository.findAllStoreTableWithOrderByStoreId(storeId);

        //Dto로 변환
        return storeTableWithOrderByStoreId.stream()
                .map(m -> m.toStoreTableFindDtoWithOrderFindDto())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createStoreTable(Long storeId) throws NoSuchStoreException {
        //db에 가게가 없으면, 예외 발생
         Store findStore = findById(storeId);

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
