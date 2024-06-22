function eliminar(id) {
    swal({
        title: "¿Estás seguro de eliminar?",
        text: "¡Una vez eliminado, no podrás recuperar este archivo imaginario!",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    })
    .then((OK) => {
        if (OK) {
            $.ajax({
                url: "/productos/delete/" + id,
                type: "GET",  // Mantenemos GET si el método del controlador es @GetMapping
                success: function(res) {
                    console.log(res);
                    swal("¡Puf! ¡Tu archivo imaginario ha sido eliminado!", {
                        icon: "success",
                    }).then((ok) => {
                        if (ok) {
                            location.href = "/productos";
                        }
                    });
                },
                error: function(xhr, status, error) {
                    console.error(xhr.responseText);
                    swal("¡Hubo un error al eliminar tu archivo!", {
                        icon: "error",
                    });
                }
            });
        } else {
            swal("¡Tu archivo imaginario está seguro!");
        }
    });
}