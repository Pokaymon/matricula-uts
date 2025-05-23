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

  const modal = document.getElementById("grupo-modal");
  const modalContent = document.querySelector(".modal-content");

  // Cerrar modal al hacer clic en el botón de cerrar
  document.querySelector(".close-modal").addEventListener("click", () => {
    modal.classList.add("hidden");
    limpiarModal();
  });

  // Cerrar modal al hacer clic fuera del contenido
  modal.addEventListener("click", (e) => {
    if (!modalContent.contains(e.target)) {
      modal.classList.add("hidden");
      limpiarModal();
    }
  });


// Crear Grupos

document.querySelector(".create_group").addEventListener("click", () => {
  limpiarModal();
  document.getElementById("crear-grupo-modal").classList.remove("hidden");
});

const crearModal = document.getElementById("crear-grupo-modal");
const crearModalContent = crearModal.querySelector(".modal-content");

crearModal.addEventListener("click", (e) => {
  if (!crearModalContent.contains(e.target)) {
    crearModal.classList.add("hidden");
    limpiarModal();
  }
});

document.getElementById("agregar-horario").addEventListener("click", () => {
  const container = document.getElementById("horarios-container");

  const index = container.children.length;
  const horarioHtml = `
    <div class="horario-item" style="border: 1px solid #ccc; padding: 10px; margin-bottom: 10px;">
      <label>Código horario: <input type="text" name="horarios[${index}].codigo" required></label><br>
      <label>Día: 
        <select name="horarios[${index}].dia" required>
          <option value="MONDAY">Lunes</option>
          <option value="TUESDAY">Martes</option>
          <option value="WEDNESDAY">Miércoles</option>
          <option value="THURSDAY">Jueves</option>
          <option value="FRIDAY">Viernes</option>
          <option value="SATURDAY">Sábado</option>
        </select>
      </label><br>
      <label>Hora inicio: <input type="time" name="horarios[${index}].horaInicio" required></label><br>
      <label>Hora fin: <input type="time" name="horarios[${index}].horaFin" required></label><br>
    </div>
  `;
  container.insertAdjacentHTML("beforeend", horarioHtml);
});

document.getElementById("form-crear-grupo").addEventListener("submit", (e) => {
  e.preventDefault();

  const form = e.target;
  const formData = new FormData(form);

  const grupo = {
    codigo: formData.get("codigo"),
    codMateria: formData.get("codMateria"),
    profesorId: formData.get("profesorId"),
    horarios: []
  };

  const horarioItems = document.querySelectorAll("#horarios-container .horario-item");
  horarioItems.forEach((item, idx) => {
    const horario = {
      codigo: item.querySelector(`[name="horarios[${idx}].codigo"]`).value,
      dia: item.querySelector(`[name="horarios[${idx}].dia"]`).value,
      horaInicio: item.querySelector(`[name="horarios[${idx}].horaInicio"]`).value + ":00",
      horaFin: item.querySelector(`[name="horarios[${idx}].horaFin"]`).value + ":00",
      codGrupo: grupo.codigo
    };
    grupo.horarios.push(horario);
  });

  fetch("/api/grupos", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(grupo)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al crear grupo");
      return res.json();
    })
    .then(data => {
      alert("Grupo creado con éxito");
      location.reload();
    })
    .catch(err => {
      console.error(err);
      alert("Error al crear grupo");
    });
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


