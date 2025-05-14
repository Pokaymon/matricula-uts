document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.getElementById("pensumTableBody");
  const modal = document.getElementById("userModal");
  const form = document.getElementById("createPensumForm");
  const modalTitle = document.getElementById("modalTittle");
  const submitButton = form.querySelector(".submit-button");
  const openBtn = document.querySelector(".create_users_button");
  const closeBtn = document.querySelector(".close-button");

  let isEditing = false;

  const limpiarFormulario = () => {
    form.reset();
    document.getElementById("pensumId").value = "";
    isEditing = false;
    modalTitle.textContent = "Crear Nuevo Pensum";
    submitButton.textContent = "Guardar Pensum";
  };

  const abrirModal = () => {
    limpiarFormulario();
    modal.style.display = "block";
  };

  const cerrarModal = () => {
    modal.style.display = "none";
    limpiarFormulario();
  };
