document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.getElementById("userTableBody");
  const modal = document.getElementById("userModal");
  const form = document.getElementById("createUserForm");
  const modalTitle = document.getElementById("modalTittle");
  const submitButton = form.querySelector(".submit-button");
  const openBtn = document.querySelector(".create_users_button");
  const closeBtn = document.querySelector(".close-button");
  const passwordField = document.getElementById("password");

  let isEditing = false;

  const limpiarFormulario = () => {
    form.reset();
    document.getElementById("userId").value = "";
    isEditing = false;
    modalTitle.textContent = "Crear Nuevo Usuario";
    submitButton.textContent = "Guardar Usuario";
    passwordField.style.display = "block";
  };

  const abrirModal = () => {
    limpiarFormulario();
    modal.style.display = "block";
  };

  const cerrarModal = () => {
    modal.style.display = "none";
    limpiarFormulario();
  };

  const renderUsuario = (user) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${user.cedula}</td>
      <td>${user.username}</td>
      <td>${user.nombre}</td>
      <td>${user.apellido}</td>
      <td>${user.rol}</td>
      <td>
        <button class="edit-btn" data-user='${JSON.stringify(user)}'>Editar</button>
        <button class="delete-btn" data-id="${user.id}">Borrar</button>
      </td>
    `;
    tbody.appendChild(row);
  };

  const cargarUsuarios = () => {
    fetch("/api/users", {
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      }
    })
    .then(res => res.json())
    .then(data => {
      tbody.innerHTML = "";
      data.forEach(renderUsuario);
    })
    .catch(err => console.error("Error al cargar usuarios:", err));
  };

  const eliminarUsuario = (id) => {
    if (confirm("¿Estás seguro de que deseas eliminar este usuario?")) {
      fetch(`/api/users/${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": "Bearer " + localStorage.getItem("token")
        }
      })
      .then(res => {
        if (!res.ok) throw new Error("Error al eliminar usuario");
        alert("Usuario eliminado con éxito");
        cargarUsuarios();
      })
      .catch(err => alert(err.message));
    }
  };

  const editarUsuario = (user) => {
    isEditing = true;
    modal.style.display = "block";
    modalTitle.textContent = "Editar Usuario";
    submitButton.textContent = "Actualizar Usuario";

    document.getElementById("userId").value = user.id;
    document.getElementById("cedula").value = user.cedula;
    document.getElementById("username").value = user.username;
    document.getElementById("password").value = user.password || "";
    document.getElementById("nombre").value = user.nombre;
    document.getElementById("apellido").value = user.apellido;
    document.getElementById("rol").value = user.rol;

    passwordField.style.display = "block";
  };

  // Event listeners globales
  openBtn.addEventListener("click", abrirModal);
  closeBtn.addEventListener("click", cerrarModal);
  window.addEventListener("click", e => e.target === modal && cerrarModal());

  document.addEventListener("click", e => {
    if (e.target.classList.contains("delete-btn")) {
      eliminarUsuario(e.target.dataset.id);
    }

    if (e.target.classList.contains("edit-btn")) {
      const user = JSON.parse(e.target.dataset.user);
      editarUsuario(user);
    }
  });

  form.addEventListener("submit", e => {
    e.preventDefault();
    const userId = document.getElementById("userId").value;

    const userData = {
      cedula: document.getElementById("cedula").value,
      username: document.getElementById("username").value,
      nombre: document.getElementById("nombre").value,
      apellido: document.getElementById("apellido").value,
      rol: document.getElementById("rol").value,
    };

    if (passwordField.value.trim()) {
      userData.password = passwordField.value;
    }

    const method = isEditing ? "PUT" : "POST";
    const url = isEditing ? `/api/users/${userId}` : "/api/users";

    fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      },
      body: JSON.stringify(userData)
    })
    .then(res => {
      if (!res.ok) throw new Error("Error al guardar usuario");
      return res.json();
    })
    .then(() => {
      alert(`Usuario ${isEditing ? "actualizado" : "creado"} con éxito`);
      cerrarModal();
      cargarUsuarios();
    })
    .catch(err => alert(err.message));
  });

  // Carga inicial
  cargarUsuarios();
});

