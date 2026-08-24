function letterFrequency(txt) {
  const freq = {};

  for (const char of txt) {
    freq[char] = (freq[char] || 0) + 1;
  }

  return Object.entries(freq).sort(
    (a, b) => a[0].codePointAt(0) - b[0].codePointAt(0)
  );
}
