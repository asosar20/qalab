package com.nttdata.stepsdefinitions;

import com.nttdata.core.Config;
import com.nttdata.core.DriverManager;
import com.nttdata.steps.*;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductStoreStepsDef {

    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;

    @Dado("estoy en la página de la tienda")
    public void estoyEnLaPaginaDeLaTienda() {
        HomeSteps homeSteps = new HomeSteps(DriverManager.getDriver());
        homeSteps.openStore();
        DriverManager.screenShot();
    }

    @Dado("me logueo con mi usuario {string} y clave {string}")
    public void meLogueoConMiUsuarioYClave(String user, String password) {
        LoginSteps loginSteps = new LoginSteps(DriverManager.getDriver());
        loginSteps.login(Config.resolve(user), Config.resolve(password));
        Assertions.assertTrue(loginSteps.isLoggedIn(),
                () -> "No se pudo acceder a la pantalla principal. Mensaje de la tienda: "
                        + loginSteps.getLoginError());
        DriverManager.screenShot();
    }

    @Cuando("navego a la categoría {string} y subcategoría {string}")
    public void navegoALaCategoriaYSubcategoria(String categoria, String subcategoria) {
        HomeSteps homeSteps = new HomeSteps(DriverManager.getDriver());

        Assertions.assertTrue(homeSteps.categoryExists(categoria),
                "La categoría '" + categoria + "' no existe en el menú de la tienda");
        homeSteps.hoverCategory(categoria);

        Assertions.assertTrue(homeSteps.subcategoryExists(subcategoria),
                "La subcategoría '" + subcategoria + "' no existe dentro de '" + categoria + "'");
        homeSteps.openSubcategory(subcategoria);

        Assertions.assertTrue(new CategorySteps(DriverManager.getDriver()).isProductListVisible(),
                "No se mostró el listado de productos de '" + subcategoria + "'");
        DriverManager.screenShot();
    }


    @Cuando("agrego {int} unidades del primer producto al carrito")
    public void agregoUnidadesDelPrimerProductoAlCarrito(int unidades) {
        CategorySteps categorySteps = new CategorySteps(DriverManager.getDriver());
        nombreProducto = categorySteps.getFirstProductName();
        String urlProducto = categorySteps.getFirstProductUrl();

        new CartSteps(DriverManager.getDriver()).emptyCart();

        categorySteps.clickFirstProduct();

        ProductSteps productSteps = new ProductSteps(DriverManager.getDriver());
        cantidad = unidades;
        productSteps.setQuantity(unidades);
        DriverManager.screenShot();
        productSteps.addToCart();
    }

    @Entonces("valido en el popup la confirmación del producto agregado")
    public void validoEnElPopupLaConfirmacionDelProductoAgregado() {
        CartModalSteps cartModalSteps = new CartModalSteps(DriverManager.getDriver());
        Assertions.assertTrue(cartModalSteps.isVisible(), "No apareció el popup de confirmación");

        String mensaje = cartModalSteps.getConfirmationMessage().toLowerCase();
        Assertions.assertTrue(mensaje.contains("producto añadido correctamente"),
                "Mensaje inesperado en el popup: " + mensaje);

        String nombrePopup = cartModalSteps.getProductName();
        Assertions.assertTrue(nombreProducto.equalsIgnoreCase(nombrePopup),
                "El producto del popup (" + nombrePopup
                        + ") no coincide con el elegido (" + nombreProducto + ")");

        Assertions.assertEquals(cantidad, cartModalSteps.getQuantity(),
                "La cantidad del popup no coincide con la solicitada");
        DriverManager.screenShot();
    }


    @Entonces("valido en el popup que el monto total sea calculado correctamente")
    public void validoEnElPopupQueElMontoTotalSeaCalculadoCorrectamente() {
        CartModalSteps cartModalSteps = new CartModalSteps(DriverManager.getDriver());
        precioUnitario = cartModalSteps.getUnitPrice();

        BigDecimal esperado = precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        Assertions.assertEquals(esperado, cartModalSteps.getSubtotal(),
                "Subtotal del popup incorrecto: " + precioUnitario + " x " + cantidad);
        Assertions.assertEquals(esperado, cartModalSteps.getTotal(),
                "Total (impuestos incl.) del popup incorrecto: " + precioUnitario + " x " + cantidad);
        DriverManager.screenShot();
    }

    @Cuando("finalizo la compra")
    public void finalizoLaCompra() {
        new CartModalSteps(DriverManager.getDriver()).proceedToCheckout();
    }

    @Entonces("valido el título de la página del carrito")
    public void validoElTituloDeLaPaginaDelCarrito() {
        String titulo = new CartSteps(DriverManager.getDriver()).getTitle().trim();
        Assertions.assertTrue("Carrito".equalsIgnoreCase(titulo),
                "Título inesperado en la página del carrito: " + titulo);
        DriverManager.screenShot();
    }

    @Entonces("vuelvo a validar el cálculo de precios en el carrito")
    public void vuelvoAValidarElCalculoDePreciosEnElCarrito() {
        CartSteps cartSteps = new CartSteps(DriverManager.getDriver());
        BigDecimal unitario = cartSteps.getUnitPrice();
        int unidades = cartSteps.getQuantity();
        BigDecimal esperado = unitario.multiply(BigDecimal.valueOf(unidades))
                .setScale(2, RoundingMode.HALF_UP);

        Assertions.assertEquals(cantidad, unidades, "La cantidad del carrito no coincide");
        Assertions.assertEquals(precioUnitario, unitario,
                "El precio unitario del carrito no coincide con el del popup");
        Assertions.assertEquals(esperado, cartSteps.getLineTotal(), "Total de la línea incorrecto");
        Assertions.assertEquals(esperado, cartSteps.getSubtotal(), "Subtotal del carrito incorrecto");
        Assertions.assertEquals(esperado, cartSteps.getTotal(), "Total (impuestos incl.) del carrito incorrecto");
        DriverManager.screenShot();
    }
}