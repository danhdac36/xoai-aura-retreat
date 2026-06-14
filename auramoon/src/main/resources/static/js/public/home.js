window.addEventListener('scroll', function() {
    const header = document.querySelector('header');
    if (window.scrollY > 80) {
        header.classList.add('navbar-scrolled');
        header.classList.remove('py-6');
        header.classList.add('py-4');
    } else {
        header.classList.remove('navbar-scrolled');
        header.classList.add('py-6');
        header.classList.remove('py-4');
    }
});
