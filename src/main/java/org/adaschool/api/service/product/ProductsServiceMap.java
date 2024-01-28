package org.adaschool.api.service.product;

import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.repository.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductsServiceMap implements ProductsService {

    private final Map<String, Product> productMap = new HashMap<>();

    @PostMapping
    public Product save(Product product) {
        String idProduct = product.getId();
        productMap.put(idProduct, product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public List<Product> all() {
        return productMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        if (!productMap.containsKey(id)) {


            throw new ProductNotFoundException(id);
        }
        productMap.remove(id);
    }

    @Override
    public Product update(Product product, String productId) {
        if (productMap.containsKey(productId)) {
            // Obtener el producto existente
            Product existingProduct = productMap.get(productId);
            // Actualizar cada atributo del producto con los valores proporcionados
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setTags(product.getTags());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setImageUrl(product.getImageUrl());

            // Update the product in the map
            productMap.put(productId, existingProduct);

            return existingProduct;
        } else { throw new ProductNotFoundException("Producto no encontrado con id: " + productId); }
    }
}