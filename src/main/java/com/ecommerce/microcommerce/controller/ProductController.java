package com.ecommerce.microcommerce.controller;

import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.Dao.ProductDao;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class ProductController {

   @Autowired
   private ProductDao productDao;

    @GetMapping("/produits")
    public MappingJacksonValue getProduit(){
        Iterable<Product> products = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeMonFiltre = new SimpleFilterProvider().addFilter("MonFiltre", monFiltre);
        MappingJacksonValue productFiltre = new MappingJacksonValue(products);
        productFiltre.setFilters(listDeMonFiltre);
        return  productFiltre;

    }

    @GetMapping(value = "/produits/{id}")
    public Product getById(@PathVariable int id){
        Product produit = productDao.findById(id);
        if (produit ==null) throw new ProduitIntrouvableException("le produit avec id :"+id+ "est introuvable");
        return produit;
    }

    @PostMapping(value = "/produits")
    public ResponseEntity<Product> addproduct(@Valid @RequestBody Product product){
        Product addProduct = productDao.save(product);
        if (Objects.isNull(addProduct)){
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addProduct.getId())
                .toUri();
        return  ResponseEntity.created(location).build();
    }
    @DeleteMapping(value = "/delete")
    public  void  deleteProduct(@PathVariable int id){
        productDao.deleteById(id);
    }

    @PutMapping (value = "/produits")
    public void updateProduit(@RequestBody Product product)
    {
        productDao.save(product);
    }


}
