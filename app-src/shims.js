function count(x) { return x.length; }
function doall(x) { return x; }
function mapIndexed(f, a) { return a.map(function(l, i) { return f(i,l);}); }
function isEqual(a, b) { return a == b; }
function partial(fn) {
  var slice = Array.prototype.slice;
  var stored_args = slice.call(arguments, 1);
  return function () {
    var new_args = slice.call(arguments);
    var args = stored_args.concat(new_args);
    return fn.apply(null, args);
  };
}
exports = {};
setTimeout(function() { animate() }, 0);
