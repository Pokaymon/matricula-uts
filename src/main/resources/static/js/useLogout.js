document.addEventListener("DOMContentLoaded", () => {
  const logoutBtn = document.getElementById("logout-btn");

  if (logoutBtn) {
    logoutBtn.addEventListener("click", function (e) {
      e.preventDefault(); // Evita que se recargue la página

      // Eliminar token del localStorage (si lo estás usando también)
      localStorage.removeItem("token");

      // Eliminar la cookie del token
      document.cookie = "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";

      // Redirigir al login
      window.location.href = "/login";
    });
  }
});
