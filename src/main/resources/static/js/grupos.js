document.addEventListener("DOMContentLoaded", () => {
  inicializarEventos();
  cargarGrupos();
});

let grupoEnEdicion = null;

function inicializarEventos() {
  const modalCrearGrupo = document.getElementById("crear-grupo-modal");
  const modalCrearContent = modalCrearGrupo.querySelector(".modal-content");

  document.querySelector(".create_group").addEventListener("click", () => {
    grupoEnEdicion = null;
    limpiarModal();
    mostrarModal(modalCrearGrupo);
  });

  modalCrearGrupo.addEventListener("click", (e) => {
    if (!modalCrearContent.contains(e.target)) ocultarModal(modalCrearGrupo);
  });

  const modalVer = document.getElementById("grupo-modal");
  const modalVerContent = modalVer.querySelector(".modal-content");

  document.querySelectorAll(".close-modal").forEach(btn => {
    btn.addEventListener("click", () => ocultarTodosLosModales());
  });

  modalVer.addEventListener("click", (e) => {
    if (!modalVerContent.contains(e.target)) ocultarModal(modalVer);
  });

  document.getElementById("agregar-horario").addEventListener("click", agregarHorario);
  document.getElementById("form-crear-grupo").addEventListener("submit", crearOActualizarGrupo);
}

function cargarGrupos() {
  fetch("/api/grupos/vista")
    .then(res => res.json())
    .then(data => {
      const container = document.querySelector(".groups");
      data.forEach(grupo => container.appendChild(crearElementoGrupo(grupo)));
    })
    .catch(err => console.error("Error cargando grupos:", err));
}

function crearElementoGrupo(grupo) {
  const div = document.createElement("div");
  div.className = "group_item";

  Object.assign(div.style, {
    display: "flex",
    justifyContent: "space-between",
    width: "100%",
    cursor: "pointer",
    position: "relative"
  });

  div.innerHTML = `
	<p>${grupo.codigo}</p>
	<p>${grupo.nombreMateria}</p>
	<div class="group-actions">
	  <span class="edit-icon" title="Editar grupo">
	    <i class="fas fa-cog"></i>
	  </span>
	  <span class="delete-icon" title="Eliminar grupo">
	    <i class="fas fa-trash-alt"></i>
	  </span>
	</div>
	`;

  div.addEventListener("click", e => {
    if (!e.target.classList.contains("delete-icon") && !e.target.closest(".edit-icon")) {
      abrirModalConDetalles(grupo.codigo);
    }
  });

  const deleteIcon = div.querySelector(".delete-icon");
  deleteIcon.addEventListener("click", e => {
    e.stopPropagation();
    confirmarYEliminarGrupo(grupo.id, div);
  });

  const editIcon = div.querySelector(".edit-icon");
  editIcon.addEventListener("click", e => {
    e.stopPropagation();

  fetch(`/api/grupos/${grupo.id}`)
    .then(res => res.json())
    .then(data => {
      grupoEnEdicion = data;
      limpiarModal();

      document.querySelector("#form-crear-grupo [name='codigo']").value = data.codigo;
      document.querySelector("#form-crear-grupo [name='codMateria']").value = data.codMateria;
      document.querySelector("#form-crear-grupo [name='profesorId']").value = data.profesorId;

      data.horarios.forEach((horario, idx) => {
        agregarHorario();
        const item = document.querySelectorAll(".horario-item")[idx];
        item.querySelector(`[name="horarios[${idx}].codigo"]`).value = horario.codigo;
        item.querySelector(`[name="horarios[${idx}].dia"]`).value = horario.dia;
        item.querySelector(`[name="horarios[${idx}].horaInicio"]`).value = horario.horaInicio.slice(0, 5);
        item.querySelector(`[name="horarios[${idx}].horaFin"]`).value = horario.horaFin.slice(0, 5);
      });

      mostrarModal(document.getElementById("crear-grupo-modal"));
    })
    .catch(err => {
      console.error("Error al obtener grupo completo:", err);
      alert("No se pudo cargar la información del grupo.");
    });
});

  return div;
}

function confirmarYEliminarGrupo(idGrupo, elemento) {
  Swal.fire({
    title: '¿Estás seguro?',
    text: "¡Esta acción no se puede deshacer!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      fetch(`/api/grupos/${idGrupo}`, {
        method: "DELETE"
      })
      .then(res => {
        if (!res.ok) throw new Error("Error al eliminar grupo");
        elemento.remove();
        Swal.fire('¡Eliminado!', 'El grupo ha sido eliminado.', 'success');
      })
      .catch(err => {
        console.error(err);
        Swal.fire('Error', 'Hubo un problema al eliminar el grupo.', 'error');
      });
    }
  });
}

function abrirModalConDetalles(codigoGrupo) {
  fetch("/api/grupos")
    .then(res => res.json())
    .then(grupos => {
      const grupo = grupos.find(g => g.codigo === codigoGrupo);
      if (!grupo) return;
      limpiarModal();
      grupo.horarios.forEach(horario => {
        marcarDia(horario.dia);
        agregarFilaHorario(horario);
      });
      mostrarModal(document.getElementById("grupo-modal"));
    })
    .catch(err => console.error("Error obteniendo detalles del grupo:", err));
}

function crearOActualizarGrupo(e) {
  e.preventDefault();

  const formData = new FormData(e.target);
  const grupo = {
    codigo: formData.get("codigo"),
    codMateria: formData.get("codMateria"),
    profesorId: formData.get("profesorId"),
    horarios: []
  };

  const items = document.querySelectorAll(".horario-item");
  items.forEach((item, idx) => {
    grupo.horarios.push({
      codigo: item.querySelector(`[name="horarios[${idx}].codigo"]`).value,
      dia: item.querySelector(`[name="horarios[${idx}].dia"]`).value,
      horaInicio: item.querySelector(`[name="horarios[${idx}].horaInicio"]`).value + ":00",
      horaFin: item.querySelector(`[name="horarios[${idx}].horaFin"]`).value + ":00",
      codGrupo: grupo.codigo
    });
  });

  const metodo = grupoEnEdicion ? "PUT" : "POST";
  const url = grupoEnEdicion ? `/api/grupos/${grupoEnEdicion.id}` : "/api/grupos";

  fetch(url, {
    method: metodo,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(grupo)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al guardar grupo");
      return res.json();
    })
    .then(() => {
      alert("Grupo guardado con éxito");
      location.reload();
    })
    .catch(err => {
      console.error(err);
      alert("Error al guardar grupo");
    });
}

function agregarHorario() {
  const container = document.getElementById("horarios-container");
  const totalHorarios = container.children.length;

  if (totalHorarios >= 2) {
    document.getElementById("agregar-horario").disabled = true;
    alert("Solo puedes agregar hasta dos horarios")
    return;
  }

  const index = totalHorarios;

  container.insertAdjacentHTML("beforeend", `
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
  `);
}

function limpiarModal() {
  document.querySelectorAll(".day").forEach(d => d.classList.remove("active"));
  document.getElementById("horario-table-body").innerHTML = "";
  document.getElementById("horarios-container").innerHTML = "";
  document.getElementById("agregar-horario").disabled = false;
  document.getElementById("form-crear-grupo").reset();
}

function mostrarModal(modal) {
  modal.classList.remove("hidden");
}

function ocultarModal(modal) {
  modal.classList.add("hidden");
}

function ocultarTodosLosModales() {
  document.querySelectorAll(".modal").forEach(m => m.classList.add("hidden"));
  limpiarModal();
}

function marcarDia(dia) {
  const diaDiv = document.querySelector(`.day[data-day="${dia}"]`);
  if (diaDiv) diaDiv.classList.add("active");
}

function agregarFilaHorario(horario) {
  const row = document.createElement("tr");
  row.innerHTML = `
    <td>${traducirDia(horario.dia)}</td>
    <td>${horario.horaInicio}</td>
    <td>${horario.horaFin}</td>
  `;
  document.getElementById("horario-table-body").appendChild(row);
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

