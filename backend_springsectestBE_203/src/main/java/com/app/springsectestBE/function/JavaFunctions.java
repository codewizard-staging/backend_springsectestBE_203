package com.app.springsectestBE.function;

import com.app.springsectestBE.model.Manager;
import com.app.springsectestBE.model.Pet;
import com.app.springsectestBE.model.PetCareCenter;
import com.app.springsectestBE.model.PetOwner;
import com.app.springsectestBE.model.Document;
import com.app.springsectestBE.model.PetService;
import com.app.springsectestBE.enums.PetServiceType;
import com.app.springsectestBE.converter.PetServiceTypeConverter;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmFunction;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmParameter;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extension.ODataFunction;
import com.app.springsectestBE.repository.PetServiceRepository;
import com.app.springsectestBE.repository.PetOwnerRepository;
import com.app.springsectestBE.repository.PetCareCenterRepository;
import com.app.springsectestBE.repository.ManagerRepository;
import com.app.springsectestBE.repository.DocumentRepository;
import com.app.springsectestBE.repository.PetRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Component
public class JavaFunctions implements ODataFunction {


    
    
}
   
