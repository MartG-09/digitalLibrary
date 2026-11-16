document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.book-cover').forEach((image) => {
    image.addEventListener('error', () => {
      image.hidden = true;
      const cover = document.createElement('div');
      cover.className = 'book-cover-placeholder';
      cover.textContent = image.alt.replace(/^Cover of\s*/i, '');
      image.after(cover);
    }, { once: true });
  });

  document.querySelectorAll('[data-table-filter]').forEach((input) => {
    const table = document.querySelector(input.dataset.tableFilter);
    if (!table) return;

    const body = table.querySelector('tbody');
    const rows = Array.from(body.querySelectorAll('tr')).filter((row) => !row.querySelector('.empty'));
    const noMatches = document.createElement('tr');
    noMatches.hidden = true;
    noMatches.innerHTML = `<td class="empty" colspan="${table.querySelectorAll('thead th').length}">No records match your search.</td>`;
    body.append(noMatches);
    input.addEventListener('input', () => {
      const query = input.value.trim().toLocaleLowerCase();
      let visibleCount = 0;
      rows.forEach((row) => {
        row.hidden = !row.textContent.toLocaleLowerCase().includes(query);
        if (!row.hidden) visibleCount += 1;
      });
      noMatches.hidden = visibleCount > 0 || !query;
    });
  });

  document.querySelectorAll('[data-password-toggle]').forEach((button) => {
    button.addEventListener('click', () => {
      const input = document.getElementById(button.dataset.passwordToggle);
      if (!input) return;
      const show = input.type === 'password';
      input.type = show ? 'text' : 'password';
      button.setAttribute('aria-pressed', String(show));
      button.textContent = show ? 'Hide' : 'Show';
    });
  });
});
