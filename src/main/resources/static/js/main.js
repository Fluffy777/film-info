function hasClass(elem, className) {
  return new RegExp(' ' + className + ' ').test(' ' + elem.className + ' ');
}

function addClass(elem, className) {
  if (!hasClass(elem, className))
  {
    elem.className += ' ' + className;
  }
}

function removeClass(elem, className) {
  let newClass = ' ' + elem.className.replace(/[\t\r\n]/g, ' ') + ' ';
  if (hasClass(elem, className)) {
    while (newClass.indexOf(' ' + className + ' ') >= 0 ) {
      newClass = newClass.replace(' ' + className + ' ', ' ');
    }
    elem.className = newClass.replace(/^\s+|\s+$/g, '');
  }
}

function switchMode(oldSet, newSet) {
  let dataName;
  addClass(newSet, "form__fieldset--selected");
  removeClass(oldSet, "form__fieldset--selected");

  oldSet.querySelectorAll(".form__input").forEach(function(item) {
    dataName = item.getAttribute("data-name");

    if (dataName != "plot") {
      item.value = "";
    } else {
      item.value = "short";
    }
    
    if (dataName != "year" && dataName != "plot") {
      item.required = false;
    }
    
    item.removeAttribute("name");
  });

  newSet.querySelectorAll(".form__input").forEach(function(item) {
    dataName = item.getAttribute("data-name");
    if (dataName != "year" && dataName != "plot") {
      item.required = true;
    }
    item.name = dataName;
  });
}

function preserveNames () {
  document.querySelectorAll(".form__input").forEach(element => {
    element.setAttribute("data-name", element.name);
  });
}

window.onload = function() {
  let form = document.forms[0];
  let fieldsets = form.querySelectorAll(".form__fieldset");
  preserveNames();

  let switchers = [function() {switchMode(fieldsets[1], fieldsets[0]);}, function() {switchMode(fieldsets[0], fieldsets[1]);}];
  fieldsets.forEach(function(item, index, array) {
    item.querySelectorAll(".form__input").forEach(
      element => {
        element.onfocus = switchers[index];
      }
    );
    item.onfocus = switchers[index];
  });

  let fieldsetFound = false;
  form.querySelectorAll('.form__input[type="text"]').forEach(function(item) {
    if (item.value.length > 0) {
      item.onfocus();
      fieldsetFound = true;
    }
  });
  if (!fieldsetFound) {
    fieldsets[0].onfocus();
  }

  form.querySelector('.form__input[data-name="year"]').oninput = function() {
    this.value = this.value.replace(/\D/gm,"");
    if (this.value.length > 4) {
      this.value = this.value.substring(0,4);
    }
  }
}