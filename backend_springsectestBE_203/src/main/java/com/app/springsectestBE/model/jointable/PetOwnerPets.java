package com.app.springsectestBE.model.jointable;

import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmIgnore;
import lombok.Data;
import javax.persistence.*;

import com.app.springsectestBE.model.Manager;
import com.app.springsectestBE.model.Pet;
import com.app.springsectestBE.model.PetCareCenter;
import com.app.springsectestBE.model.PetOwner;
import com.app.springsectestBE.model.Document;
import com.app.springsectestBE.model.PetService;
import com.app.springsectestBE.enums.PetServiceType;
import com.app.springsectestBE.converter.PetServiceTypeConverter;

@Entity(name = "PetOwnerPets")
@Table(schema = "\"springsectestbe_047\"", name = "\"PetOwnerPets\"")
@Data
public class PetOwnerPets{

 	@Id
    @Column(name = "\"Id\"")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "\"OwnerId\"")
	private Integer ownerId;

    
    @Column(name = "\"PetId\"")
    private Integer petId;
 
}