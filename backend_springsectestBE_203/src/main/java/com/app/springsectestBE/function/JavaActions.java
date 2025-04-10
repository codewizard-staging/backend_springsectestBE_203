package com.app.springsectestBE.function;

import com.app.springsectestBE.model.Manager;
import com.app.springsectestBE.model.Pet;
import com.app.springsectestBE.model.PetCareCenter;
import com.app.springsectestBE.model.PetOwner;
import com.app.springsectestBE.model.Document;
import com.app.springsectestBE.model.PetService;




import com.app.springsectestBE.enums.PetServiceType;
import com.app.springsectestBE.converter.PetServiceTypeConverter;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmAction;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmParameter;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extension.ODataAction;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

@Component
public class JavaActions implements ODataAction {
    private final EntityManager entityManager;

    public JavaActions(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

	
	
}
  