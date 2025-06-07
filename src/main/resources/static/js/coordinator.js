document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.getElementById("pensumTableBody");
  const modal = document.getElementById("userModal");
  const form = document.getElementById("createPensumForm");
  const modalTitle = document.getElementById("modalTittle");
  const submitButton = form.querySelector(".submit-button");
  const openBtn = document.querySelector(".create_users_button");
  const closeBtn = document.querySelector(".close-button");
  const materiasContainer = document.getElementById("materias-checkboxes");
  const carreraSelect = document.getElementById("carrera");

  let isEditing = false;

  const limpiarFormulario = () => {
    form.reset();
    document.getElementById("pensumId").value = "";
    isEditing = false;
    modalTitle.textContent = "Crear Nuevo Pensum";
    submitButton.textContent = "Guardar Pensum";
    document.querySelectorAll(".materia-checkbox").forEach(cb => cb.checked = false);
  };

  const abrirModal = () => {
    limpiarFormulario();
    modal.style.display = "block";
  };

  const cerrarModal = () => {
    modal.style.display = "none";
    limpiarFormulario();
  };

  const renderPensum = (pensum) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${pensum.nombreCarrera || "Sin carrera"}</td>
      <td>${pensum.codigo}</td>
      <td>${pensum.fechaInicio}</td>
      <td>${pensum.activo ? 'Sí' : 'No'}</td>
      <td>${(pensum.materias || []).join(", ")}</td>
      <td>
        <button class="edit-btn" data-pensum='${JSON.stringify(pensum)}'>Editar</button>
        <button class="delete-btn" data-id="${pensum.id}">Borrar</button>
      </td>
    `;
    tbody.appendChild(row);
  };

  const cargarPensums = () => {
    fetch("/api/pensums", {
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      }
    })
      .then(res => res.json())
      .then(data => {
        if (!Array.isArray(data)) {
          console.error("Respuesta inesperada:", data);
          throw new Error("La respuesta del servidor no es una lista");
        }
        tbody.innerHTML = "";
        data.forEach(renderPensum);
      })
      .catch(err => console.error("Error al cargar pensums:", err));
  };

  const cargarMaterias = (materiasSeleccionadas = []) => {
    fetch("/api/materias", {
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      }
    })
      .then(res => res.json())
      .then(data => {
        materiasContainer.innerHTML = "";
        data.forEach(m => {
          const label = document.createElement("label");
          label.style.display = "block";

          const checkbox = document.createElement("input");
          checkbox.type = "checkbox";
          checkbox.value = m.codigo;
          checkbox.classList.add("materia-checkbox");

          if (materiasSeleccionadas.includes(m.codigo)) {
            checkbox.checked = true;
          }

          label.appendChild(checkbox);
          label.appendChild(document.createTextNode(`${m.codigo} - ${m.nombre}`));
          materiasContainer.appendChild(label);
        });
      })
      .catch(err => console.error("Error al cargar materias:", err));
  };

  const cargarCarreras = (carreraSeleccionadaId = "") => {
    fetch("/api/carreras", {
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      }
    })
      .then(res => res.json())
      .then(data => {
        carreraSelect.innerHTML = '<option value="">Seleccione una carrera</option>';
        data.forEach(carrera => {
          const option = document.createElement("option");
          option.value = carrera.id;
          option.textContent = carrera.nombre;
          if (carrera.id === carreraSeleccionadaId) {
            option.selected = true;
          }
          carreraSelect.appendChild(option);
        });
      })
      .catch(err => console.error("Error al cargar carreras:", err));
  };

  const eliminarPensum = (id) => {
    if (confirm("¿Estás seguro de que deseas eliminar este pensum?")) {
      fetch(`/api/pensums/${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": "Bearer " + localStorage.getItem("token")
        }
      })
        .then(res => {
          if (!res.ok) throw new Error("Error al eliminar pensum");
          alert("Pensum eliminado con éxito");
          cargarPensums();
        })
        .catch(err => alert(err.message));
    }
  };

  const editarPensum = (pensum) => {
    isEditing = true;
    modal.style.display = "block";
    modalTitle.textContent = "Editar Pensum";
    submitButton.textContent = "Actualizar Pensum";

    document.getElementById("pensumId").value = pensum.id;
    document.getElementById("codigo").value = pensum.codigo;
    document.getElementById("fechaInicio").value = pensum.fechaInicio;
    document.getElementById("activo").checked = pensum.activo;

    cargarCarreras(pensum.idCarrera); // Usamos el ID de carrera para seleccionarla
    cargarMaterias(pensum.materias || []);
  };

  openBtn.addEventListener("click", () => {
    cargarCarreras();
    cargarMaterias();
    abrirModal();
  });

  closeBtn.addEventListener("click", cerrarModal);
  window.addEventListener("click", e => e.target === modal && cerrarModal());

  document.addEventListener("click", e => {
    if (e.target.classList.contains("delete-btn")) {
      eliminarPensum(e.target.dataset.id);
    }

    if (e.target.classList.contains("edit-btn")) {
      const pensum = JSON.parse(e.target.dataset.pensum);
      editarPensum(pensum);
    }
  });

  form.addEventListener("submit", e => {
    e.preventDefault();

    const materiasSeleccionadas = Array.from(document.querySelectorAll(".materia-checkbox:checked")).map(cb => cb.value);

    if (materiasSeleccionadas.length === 0) {
      alert("Debes seleccionar al menos una materia para continuar.");
      return;
    }

    const pensumId = document.getElementById("pensumId").value;
    const pensumData = {
      carrera: carreraSelect.value, // ID seleccionado
      codigo: document.getElementById("codigo").value,
      fechaInicio: document.getElementById("fechaInicio").value,
      activo: document.getElementById("activo").checked,
      materias: materiasSeleccionadas
    };

    const method = isEditing ? "PUT" : "POST";
    const url = isEditing ? `/api/pensums/${pensumId}` : "/api/pensums";

    fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      },
      body: JSON.stringify(pensumData)
    })
      .then(res => {
        if (!res.ok) throw new Error("Error al guardar pensum");
        return res.json();
      })
      .then(() => {
        alert(`Pensum ${isEditing ? "actualizado" : "creado"} con éxito`);
        cerrarModal();
        cargarPensums();
      })
      .catch(err => alert(err.message));
  });

  cargarPensums();
  cargarMaterias();
  cargarCarreras();
});

