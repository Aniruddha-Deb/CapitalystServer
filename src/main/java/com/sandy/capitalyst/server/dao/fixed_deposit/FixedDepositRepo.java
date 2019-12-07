package com.sandy.capitalyst.server.dao.fixed_deposit;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

public interface FixedDepositRepo 
    extends CrudRepository<FixedDeposit, Integer> {
    
    @Query( value =   
            "SELECT "
          + "    fd "
          + "FROM "
          + "    FixedDeposit fd "
          + "WHERE "
          + "    fd.matureDate >= CURRENT_DATE "
          + "ORDER BY "
          + "    fd.baseAccount.accountOwner ASC, "
          + "    fd.matureDate DESC "
    )
    List<FixedDeposit> findAllActiveDeposits() ;
}
