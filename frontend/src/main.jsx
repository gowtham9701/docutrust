import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'

const api = `${import.meta.env.VITE_API_BASE_URL || ''}/api/v1/documents`

function App() {
  const [documents, setDocuments] = useState([])
  const [selected, setSelected] = useState(null)
  const [form, setForm] = useState({ fileName: '', content: '' })
  const [file, setFile] = useState(null)
  const [error, setError] = useState('')

  const load = async () => {
    const response = await fetch(api)
    if (!response.ok) throw new Error('Could not load documents')
    setDocuments(await response.json())
  }

  useEffect(() => { load().catch((err) => setError(err.message)) }, [])
  useEffect(() => {
    if (!selected || selected.status !== 'ANALYZING') return
    const timer = setInterval(() => load().then(() => fetch(`${api}/${selected.id}`).then(r => r.json()).then(setSelected)).catch(() => {}), 1500)
    return () => clearInterval(timer)
  }, [selected])

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    const request = file
      ? fetch(`${api}/upload`, { method: 'POST', body: (() => { const data = new FormData(); data.append('file', file); return data })() })
      : fetch(api, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form) })
    const response = await request
    if (!response.ok) { setError('Please provide a file name and some text.'); return }
    const document = await response.json()
    setForm({ fileName: '', content: '' })
    setFile(null)
    setSelected(document)
    await load()
  }

  return <main>
    <header><div className="eyebrow">DOCUMENT INTELLIGENCE</div><h1>Docu<span>Trust</span></h1><p>Make every document decision explainable.</p></header>
    <section className="grid">
      <form className="card submit-card" onSubmit={submit}>
        <h2>Submit a document</h2>
        <label>Upload text/JSON file<input type="file" accept=".txt,.md,.json,text/plain,application/json" onChange={e => { const picked = e.target.files?.[0] || null; setFile(picked); if (picked) setForm({ ...form, fileName: picked.name }) }} /></label>
        <label>File name<input required value={form.fileName} onChange={e => { setFile(null); setForm({ ...form, fileName: e.target.value }) }} placeholder="vendor-agreement.txt" /></label>
        <label>Text content<textarea required={!file} rows="8" value={form.content} onChange={e => setForm({ ...form, content: e.target.value })} placeholder="Paste document text for analysis..." /></label>
        <button type="submit">Analyze document <span>→</span></button>
        {error && <div className="error">{error}</div>}
      </form>
      <section className="card">
        <div className="section-heading"><h2>Recent documents</h2><span className="count">{documents.length}</span></div>
        {documents.length === 0 ? <div className="empty">Your submitted documents will appear here.</div> :
          <div className="documents">{documents.map(document => <button className={`document ${selected?.id === document.id ? 'active' : ''}`} key={document.id} onClick={() => setSelected(document)}>
            <span className="file-icon">DOC</span><span className="document-info"><strong>{document.fileName}</strong><small>{new Date(document.submittedAt).toLocaleString()}</small></span><span className={`status ${document.status.toLowerCase()}`}>{document.status}</span>
          </button>)}</div>}
      </section>
    </section>
    {selected && <section className="card findings"><div className="section-heading"><div><h2>Findings</h2><p>{selected.fileName}</p></div><span className={`status ${selected.status.toLowerCase()}`}>{selected.status}</span></div>
      {selected.findings.map(finding => <article className="finding" key={finding.id}><div className={`severity ${finding.severity.toLowerCase()}`}>{finding.severity}</div><div><h3>{finding.title}</h3><p>{finding.explanation}</p><small>{finding.category}</small></div></article>)}</section>}
  </main>
}

createRoot(document.getElementById('root')).render(<App />)
