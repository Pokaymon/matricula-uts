document.addEventListener("DOMContentLoaded", () => {
  fetch("/api/grupos/vista")
    .then(res => res.json())
    .then(data => {
      const groupsContainer = document.querySelector(".groups");

      data.forEach(grupo => {
        const grupoDiv = document.createElement("div");
        grupoDiv.classList.add("group_item");
        Object.assign(grupoDiv.style, {
          display: "flex",
          justifyContent: "space-between",
          width: "100%",
          cursor: "pointer"
        });

        grupoDiv.innerHTML = `
          <p>${grupo.codigo}</p>
          <p>${grupo.nombreMateria}</p>
        `;

        grupoDiv.addEventListener("click", () => {
          abrirModalConDetalles(grupo.codigo);
        });

        groupsContainer.appendChild(grupoDiv);
      });
    })
    .catch(err => {
      console.error("Error cargando grupos:", err);
    });

  // Cerrar modal
  document.querySelector(".close-modal").addEventListener("click", () => {
    document.getElementById("grupo-modal").classList.add("hidden");
    limpiarModal();
  });
});

function abrirModalConDetalles(codigoGrupo) {
  fetch(`/api/grupos`)
    .then(res => res.json())
    .then(grupos => {
      const grupo = grupos.find(g => g.codigo === codigoGrupo);
      if (!grupo) return;

      // Limpiar estado anterior
      limpiarModal();

      // Marcar días activos
      grupo.horarios.forEach(horario => {
        const diaDiv = document.querySelector(`.day[data-day="${horario.dia}"]`);
        if (diaDiv) diaDiv.classList.add("active");

        // Agregar fila a la tabla
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${traducirDia(horario.dia)}</td>
          <td>${horario.horaInicio}</td>
          <td>${horario.horaFin}</td>
        `;
        document.getElementById("horario-table-body").appendChild(row);
      });

      document.getElementById("grupo-modal").classList.remove("hidden");
    })
    .catch(err => console.error("Error obteniendo detalles del grupo:", err));
}

function limpiarModal() {
  document.querySelectorAll(".day").forEach(d => d.classList.remove("active"));
  document.getElementById("horario-table-body").innerHTML = "";
}

function traducirDia(diaIngles) {
  const mapa = {
    MONDAY: "Lunes",
    TUESDAY: "Martes",
    WEDNESDAY: "Miércoles",
    THURSDAY: "Jueves",
    FRIDAY: "Viernes",
    SATURDAY: "Sábado"
  };
  return mapa[diaIngles] || diaIngles;
}

