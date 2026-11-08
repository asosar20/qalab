# language: es
@store
Característica: Product - Store

  @precioProducto
  Esquema del escenario: Validación del precio de un producto
    Dado estoy en la página de la tienda
    Y me logueo con mi usuario "<usuario>" y clave "<clave>"
    Cuando navego a la categoría "<categoria>" y subcategoría "<subcategoria>"
    Y agrego <cantidad> unidades del primer producto al carrito
    Entonces valido en el popup la confirmación del producto agregado
    Y valido en el popup que el monto total sea calculado correctamente
    Cuando finalizo la compra
    Entonces valido el título de la página del carrito
    Y vuelvo a validar el cálculo de precios en el carrito

    @casoValido
    Ejemplos: Login válido
      | usuario       | clave         | categoria | subcategoria | cantidad |
      | ${VALID_USER} | ${VALID_PASS} | Clothes   | Men          | 2        |

    @loginInvalido
    Ejemplos: Login inválido
      | usuario            | clave            | categoria | subcategoria | cantidad |
      | invalido@gmail.com | clave_incorrecta | Clothes   | Men          | 2        |

    @categoriaInexistente
    Ejemplos: Categoría inexistente
      | usuario       | clave         | categoria | subcategoria | cantidad |
      | ${VALID_USER} | ${VALID_PASS} | Autos     | Men          | 2        |