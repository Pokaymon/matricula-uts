document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.getElementById("pensumTableBody");
  const modal = document.getElementById("userModal");
  const form = document.getElementById("createPensumForm");
  const modalTitle = document.getElementById("modalTittle");
  const submitButton = form.querySelector(".submit-button");
  const openBtn = document.querySelector(".create_users_button");
  const closeBtn = document.querySelector(".close-button");
  const materiasSelect = document.getElementById("materias");

  let isEditing = false;

  const limpiarFormulario = () => {
    form.reset();
    document.getElementById("pensumId").value = "";
    isEditing = false;
    modalTitle.textContent = "Crear Nuevo Pensum";
    submitButton.textContent = "Guardar Pensum";
    [...materiasSelect.options].forEach(option => option.selected = false);
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
      <td>${pensum.carrera}</td>
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
        tbody.innerHTML = "";
        data.forEach(renderPensum);
      })
      .catch(err => console.error("Error al cargar pensums:", err));
  };

  const cargarMaterias = () => {
    fetch("/api/materias", {
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      }
    })
      .then(res => res.json())
      .then(data => {
        materiasSelect.innerHTML = "";
        data.forEach(m => {
          const option = document.createElement("option");
          option.value = m.codigo;
          option.textContent = `${m.codigo} - ${m.nombre}`;
          materiasSelect.appendChild(option);
        });
      })
      .catch(err => console.error("Error al cargar materias:", err));
  };

  const eliminarPensum = (id) => {
    if (confirm("¿Estás seguro de que deseas eliminar este pensum?")) {
      fetch(`/api/pensums/${id}`, { method: "DELETE",
	headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
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
    document.getElementById("carrera").value = pensum.carrera;
    document.getElementById("codigo").value = pensum.codigo;
    document.getElementById("fechaInicio").value = pensum.fechaInicio;
    document.getElementById("activo").checked = pensum.activo;

    [...materiasSelect.options].forEach(opt => {
      opt.selected = pensum.materias.includes(opt.value);
    });
  };

  openBtn.addEventListener("click", () => {
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
      cargarMaterias();
      const pensum = JSON.parse(e.target.dataset.pensum);
      editarPensum(pensum);
    }
  });

  form.addEventListener("submit", e => {
    e.preventDefault();

    const pensumId = document.getElementById("pensumId").value;
    const pensumData = {
      carrera: document.getElementById("carrera").value,
      codigo: document.getElementById("codigo").value,
      fechaInicio: document.getElementById("fechaInicio").value,
      activo: document.getElementById("activo").checked,
      materias: Array.from(materiasSelect.selectedOptions).map(opt => opt.value)
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

  // Carga inicial
  cargarPensums();
  cargarMaterias();
});

