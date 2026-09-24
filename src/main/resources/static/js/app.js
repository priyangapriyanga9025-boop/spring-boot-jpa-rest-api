const API = '/api';
const tokenKey = 'workforce_token';
const userKey = 'workforce_user';
const roleKey = 'workforce_role';
const state = { employees: [], departments: [] };

const $ = (selector) => document.querySelector(selector);
const token = () => sessionStorage.getItem(tokenKey);
const show = (selector, visible) => $(selector).classList.toggle('hidden', !visible);
const message = (selector, text = '', error = true) => { const element = $(selector); element.textContent = text; element.className = `message${text ? (error ? ' error' : ' success') : ''}`; };

class ApiError extends Error { constructor(message, status, details = {}) { super(message); this.status = status; this.details = details; } }

async function request(path, options = {}) {
    const headers = { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(options.headers || {}) };
    if (token()) headers.Authorization = `Bearer ${token()}`;
    const response = await fetch(`${API}${path}`, { ...options, headers });
    const data = response.status === 204 ? null : await response.json().catch(() => ({}));
    if (response.status === 401) { leaveApp(); throw new ApiError('Your session has expired. Please sign in again.', 401); }
    if (!response.ok) { const details = data?.details ? ` ${Object.values(data.details).join(' ')}` : ''; throw new ApiError(`${data?.message || 'The server could not complete that request.'}${details}`, response.status, data?.details); }
    return data;
}

function enterApp() { show('#auth-view', false); show('#app-view', true); $('#current-user').textContent = `${sessionStorage.getItem(userKey) || 'User'} / ${sessionStorage.getItem(roleKey) || 'USER'}`; loadDashboard(); }
function leaveApp() { sessionStorage.clear(); show('#app-view', false); show('#auth-view', true); message('#auth-message'); }
function setBusy(form, busy) { form.querySelectorAll('button').forEach(button => { button.disabled = busy; }); }
function formData(form) { return Object.fromEntries(new FormData(form)); }

async function loadDashboard() {
    $('#employee-rows').innerHTML = '<tr><td colspan="5" class="empty">Loading workforce data...</td></tr>';
    $('#department-list').innerHTML = '<p class="empty">Loading departments...</p>';
    try { [state.employees, state.departments] = await Promise.all([request('/employees'), request('/departments')]); renderDashboard(); message('#app-message'); }
    catch (error) { if (error.status !== 401) message('#app-message', error.message); }
}

function renderDashboard() {
    const search = ($('#employee-search').value || '').toLowerCase();
    const departmentId = $('#employee-filter-department').value;
    const employees = state.employees.filter(employee => { const text = `${employee.name} ${employee.email} ${employee.role}`.toLowerCase(); return (!search || text.includes(search)) && (!departmentId || String(employee.department?.id) === departmentId); });
    $('#employee-count').textContent = state.employees.length;
    $('#department-count').textContent = state.departments.length;
    const average = state.employees.length ? state.employees.reduce((sum, employee) => sum + Number(employee.salary), 0) / state.employees.length : 0;
    $('#average-salary').textContent = average ? `$${Math.round(average).toLocaleString()}` : '$0';
    $('#employee-rows').innerHTML = employees.length ? employees.map(employee => `<tr><td>${escapeHtml(employee.name)}<br><small>${escapeHtml(employee.email)}</small></td><td>${escapeHtml(employee.role)}</td><td>${escapeHtml(employee.department?.name || 'Unassigned')}</td><td>$${Number(employee.salary).toLocaleString()}</td><td class="actions"><button class="table-button" data-edit-employee="${employee.id}">Edit</button><button class="delete-button" data-delete-employee="${employee.id}">Delete</button></td></tr>`).join('') : `<tr><td colspan="5" class="empty">${state.employees.length ? 'No employees match this search.' : 'No employees have been added yet.'}</td></tr>`;
    $('#department-list').innerHTML = state.departments.length ? state.departments.map(department => `<div class="department-item"><strong>${escapeHtml(department.name)}</strong><span>${state.employees.filter(employee => employee.department?.id === department.id).length} PEOPLE <button class="table-button" data-edit-department="${department.id}">Edit</button><button class="delete-button" data-delete-department="${department.id}">Delete</button></span></div>`).join('') : '<p class="empty">No departments have been added yet.</p>';
    $('#employee-department').innerHTML = state.departments.map(department => `<option value="${department.id}">${escapeHtml(department.name)}</option>`).join('');
    $('#employee-filter-department').innerHTML = '<option value="">All departments</option>' + state.departments.map(department => `<option value="${department.id}">${escapeHtml(department.name)}</option>`).join('');
    $('#employee-filter-department').value = departmentId;
}
function escapeHtml(value) { return String(value ?? '').replace(/[&<>'"]/g, character => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' }[character])); }
function openEmployeeForm(employee = null) { const form = $('#employee-record-form'); form.reset(); form.elements.id.value = employee?.id || ''; form.elements.name.value = employee?.name || ''; form.elements.email.value = employee?.email || ''; form.elements.role.value = employee?.role || ''; form.elements.salary.value = employee?.salary || ''; form.elements.departmentId.value = employee?.department?.id || state.departments[0]?.id || ''; $('#employee-form-eyebrow').textContent = employee ? 'EDIT RECORD' : 'NEW RECORD'; $('#employee-form-title').textContent = employee ? 'Edit employee' : 'Add employee'; $('#employee-submit-label').textContent = employee ? 'Update employee' : 'Save employee'; show('#employee-form', true); }
function openDepartmentForm(department = null) { const form = $('#department-record-form'); form.reset(); form.elements.id.value = department?.id || ''; form.elements.name.value = department?.name || ''; $('#department-form-eyebrow').textContent = department ? 'EDIT RECORD' : 'NEW RECORD'; $('#department-form-title').textContent = department ? 'Edit department' : 'Add department'; $('#department-submit-label').textContent = department ? 'Update department' : 'Save department'; show('#department-form', true); }

$('#login-form').addEventListener('submit', async event => { event.preventDefault(); const form = event.target; setBusy(form, true); try { const data = await request('/auth/login', { method: 'POST', body: JSON.stringify(formData(form)) }); sessionStorage.setItem(tokenKey, data.token); sessionStorage.setItem(userKey, data.username); sessionStorage.setItem(roleKey, data.role); form.reset(); enterApp(); } catch (error) { message('#auth-message', error.message); } finally { setBusy(form, false); } });
$('#register-form').addEventListener('submit', async event => { event.preventDefault(); const form = event.target; setBusy(form, true); try { await request('/auth/register', { method: 'POST', body: JSON.stringify(formData(form)) }); form.reset(); message('#auth-message', 'Account created. Sign in to continue.', false); $('[data-auth-tab="login"]').click(); } catch (error) { message('#auth-message', error.message); } finally { setBusy(form, false); } });
$('#employee-record-form').addEventListener('submit', async event => { event.preventDefault(); const form = event.target; const values = formData(form); const id = values.id; delete values.id; values.salary = Number(values.salary); values.departmentId = Number(values.departmentId); setBusy(form, true); try { await request(id ? `/employees/${id}` : '/employees', { method: id ? 'PUT' : 'POST', body: JSON.stringify(values) }); show('#employee-form', false); message('#app-message', id ? 'Employee updated.' : 'Employee added.', false); await loadDashboard(); } catch (error) { message('#app-message', error.message); } finally { setBusy(form, false); } });
$('#department-record-form').addEventListener('submit', async event => { event.preventDefault(); const form = event.target; const values = formData(form); const id = values.id; delete values.id; setBusy(form, true); try { await request(id ? `/departments/${id}` : '/departments', { method: id ? 'PUT' : 'POST', body: JSON.stringify(values) }); show('#department-form', false); message('#app-message', id ? 'Department updated.' : 'Department added.', false); await loadDashboard(); } catch (error) { message('#app-message', error.message); } finally { setBusy(form, false); } });

document.addEventListener('click', async event => {
    const tab = event.target.closest('[data-auth-tab]'); if (tab) { document.querySelectorAll('.tab').forEach(button => button.classList.remove('active')); tab.classList.add('active'); const register = tab.dataset.authTab === 'register'; show('#login-form', !register); show('#register-form', register); $('#auth-title').textContent = register ? 'Create your account' : 'Welcome back'; $('#auth-subtitle').textContent = register ? 'Set up access to your operations workspace.' : 'Sign in to continue to your operations dashboard.'; message('#auth-message'); return; }
    const open = event.target.closest('[data-open]'); if (open) { if (open.dataset.open === 'employee-form') openEmployeeForm(); else openDepartmentForm(); return; }
    const close = event.target.closest('[data-close]'); if (close) { show(`#${close.dataset.close}`, false); return; }
    if (event.target.closest('#logout-button')) { leaveApp(); return; }
    const employeeEdit = event.target.closest('[data-edit-employee]'); if (employeeEdit) { openEmployeeForm(state.employees.find(employee => String(employee.id) === employeeEdit.dataset.editEmployee)); return; }
    const departmentEdit = event.target.closest('[data-edit-department]'); if (departmentEdit) { openDepartmentForm(state.departments.find(department => String(department.id) === departmentEdit.dataset.editDepartment)); return; }
    const employeeId = event.target.closest('[data-delete-employee]')?.dataset.deleteEmployee; const departmentId = event.target.closest('[data-delete-department]')?.dataset.deleteDepartment;
    try { if (employeeId && confirm('Delete this employee record?')) { await request(`/employees/${employeeId}`, { method: 'DELETE' }); message('#app-message', 'Employee deleted.', false); await loadDashboard(); } if (departmentId && confirm('Delete this department? It must not contain employees.')) { await request(`/departments/${departmentId}`, { method: 'DELETE' }); message('#app-message', 'Department deleted.', false); await loadDashboard(); } } catch (error) { message('#app-message', error.message); }
});
$('#employee-search').addEventListener('input', renderDashboard); $('#employee-filter-department').addEventListener('change', renderDashboard); $('#clear-employee-search').addEventListener('click', () => { $('#employee-search').value = ''; $('#employee-filter-department').value = ''; renderDashboard(); });
if (token()) enterApp();
