import { motion } from 'framer-motion';
import { Mountain, Trees, Hexagon, Component, Package, Shield, ExternalLink, Play } from 'lucide-react';

import bgLandscape from './assets/catan_landscape.jpg';
import bgPieces from './assets/catan_pieces.jpg';
import boardGame from './assets/catan_board_game.jpg';

function App() {
  return (
    <div className="min-h-screen bg-[#1a1a1a] text-white overflow-x-hidden font-sans selection:bg-catan-gold selection:text-black">
      {/* Navigation */}
      <nav className="fixed top-0 w-full z-50 glass-panel border-b border-white/10">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Hexagon className="w-8 h-8 text-catan-gold fill-catan-gold/10" />
            <span className="text-xl font-bold tracking-wider">CATAN EN LÍNEA</span>
          </div>
          <div className="hidden md:flex gap-8 text-sm font-medium text-white/70">
            <a href="#historia" className="hover:text-catan-gold transition-colors">La Historia</a>
            <a href="#explora" className="hover:text-catan-gold transition-colors">Explora</a>
            <a href="#comenzar" className="hover:text-catan-gold transition-colors">Comenzar</a>
          </div>
          <button className="bg-catan-red hover:bg-catan-red/90 text-white px-6 py-2.5 rounded-full font-semibold transition-all shadow-[0_0_20px_rgba(210,59,42,0.4)] hover:shadow-[0_0_30px_rgba(210,59,42,0.6)]">
            JUGAR AHORA
          </button>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative h-screen flex items-center justify-center">
        {/* Background Image with Parallax-like effect */}
        <div 
          className="absolute inset-0 z-0 bg-cover bg-center bg-no-repeat"
          style={{ backgroundImage: `url(${bgLandscape})`, backgroundAttachment: 'fixed' }}
        />
        {/* Gradient Overlay */}
        <div className="absolute inset-0 z-10 bg-gradient-to-b from-[#1a1a1a]/60 via-[#1a1a1a]/40 to-[#1a1a1a] shadow-inner" />

        <div className="relative z-20 max-w-5xl mx-auto px-6 text-center mt-20">
          <motion.h1 
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, ease: "easeOut" }}
            className="text-6xl md:text-8xl font-black mb-6 tracking-tight drop-shadow-2xl"
          >
            Conquista la<br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-catan-gold via-yellow-400 to-catan-gold">
              Isla de Catan
            </span>
          </motion.h1>
          
          <motion.p 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.2, ease: "easeOut" }}
            className="text-xl md:text-2xl text-white/80 mb-10 max-w-3xl mx-auto font-light leading-relaxed drop-shadow-lg"
          >
            Fundar, comerciar y construir. Embárcate en una aventura online para colonizar la isla más famosa del mundo de los juegos de mesa.
          </motion.p>
          
          <motion.div 
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.5, delay: 0.4 }}
            className="flex flex-col sm:flex-row items-center justify-center gap-4"
          >
            <button className="flex items-center gap-2 bg-catan-gold hover:bg-yellow-400 text-black px-8 py-4 rounded-full font-bold text-lg transition-all shadow-[0_0_30px_rgba(242,169,0,0.5)] transform hover:-translate-y-1">
              <Play className="w-5 h-5 fill-black" />
              Empieza tu partida
            </button>
            <button className="flex items-center gap-2 glass-panel hover:bg-white/20 px-8 py-4 rounded-full font-bold text-lg transition-all">
              <ExternalLink className="w-5 h-5" />
              Saber más
            </button>
          </motion.div>
        </div>
        
        {/* Scroll Indicator */}
        <motion.div 
          animate={{ y: [0, 10, 0] }}
          transition={{ repeat: Infinity, duration: 2 }}
          className="absolute bottom-10 z-20 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2 opacity-60"
        >
          <span className="text-xs uppercase tracking-widest font-medium text-white/80">Descubre</span>
          <div className="w-px h-12 bg-gradient-to-b from-white to-transparent" />
        </motion.div>
      </section>

      {/* The Lore / About Section */}
      <section id="historia" className="py-24 relative overflow-hidden bg-[#1a1a1a]">
        <div className="absolute top-0 right-0 w-[800px] h-[800px] bg-catan-brown/10 rounded-full blur-[120px] -translate-y-1/2 translate-x-1/3" />
        <div className="absolute bottom-0 left-0 w-[600px] h-[600px] bg-catan-green/10 rounded-full blur-[100px] translate-y-1/3 -translate-x-1/3" />
        
        <div className="max-w-7xl mx-auto px-6 relative z-10">
          <div className="grid lg:grid-cols-2 gap-16 items-center">
            
            <motion.div 
              initial={{ opacity: 0, x: -50 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true, margin: "-100px" }}
              transition={{ duration: 0.7 }}
              className="space-y-8"
            >
              <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full border border-catan-red/30 bg-catan-red/10 text-catan-red font-medium text-sm">
                <Mountain className="w-4 h-4" /> Un Nuevo Mundo
              </div>
              <h2 className="text-4xl md:text-5xl font-bold leading-tight">
                Pioneros de una <br/> tierra sagrada
              </h2>
              <p className="text-lg text-white/60 leading-relaxed font-light">
                Tras una larga y agotadora travesía por mares inexplorados, vuestros barcos divisan por fin una costa. Ha llegado el momento de desembarcar, colonizar y construir carreteras, pero la isla no está deshabitada.
              </p>
              
              <div className="grid sm:grid-cols-2 gap-6 pt-6">
                <div className="glass-panel p-6 rounded-2xl hover:bg-white/5 transition-colors">
                  <Trees className="w-8 h-8 text-catan-green mb-4" />
                  <h3 className="text-xl font-bold mb-2">Comercia</h3>
                  <p className="text-sm text-white/50">Intercambia materias primas con otros jugadores: madera, arcilla, lana, cereales y mineral.</p>
                </div>
                <div className="glass-panel p-6 rounded-2xl hover:bg-white/5 transition-colors">
                  <Component className="w-8 h-8 text-catan-clay mb-4" />
                  <h3 className="text-xl font-bold mb-2">Construye</h3>
                  <p className="text-sm text-white/50">Expande tus poblados y mejóralos a ciudades. Crea la red de carreteras más larga.</p>
                </div>
              </div>
            </motion.div>

            <motion.div 
              initial={{ opacity: 0, x: 50 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true, margin: "-100px" }}
              transition={{ duration: 0.7 }}
              className="relative"
            >
              {/* Decorative elements around image */}
              <div className="absolute -inset-4 bg-gradient-to-tr from-catan-gold/30 to-catan-red/30 rounded-3xl blur-2xl" />
              <div className="relative rounded-3xl overflow-hidden border border-white/10 shadow-2xl">
                <img 
                  src={bgPieces} 
                  alt="Componentes de Catan" 
                  className="w-full h-auto object-cover hover:scale-105 transition-transform duration-700"
                />
              </div>
              {/* Floating Badge */}
              <motion.div 
                initial={{ y: 20, opacity: 0 }}
                whileInView={{ y: 0, opacity: 1 }}
                viewport={{ once: true }}
                transition={{ delay: 0.5 }}
                className="absolute -bottom-8 -left-8 glass-panel p-6 rounded-2xl max-w-[200px]"
              >
                <Package className="w-6 h-6 text-catan-gold mb-3" />
                <p className="text-sm font-medium">Recursos limitados, batallas puramente estratégicas.</p>
              </motion.div>
            </motion.div>

          </div>
        </div>
      </section>

      {/* Epic Visual / Full width image section */}
      <section id="explora" className="relative py-32 border-y border-white/5">
        <div className="absolute inset-0">
          <img src={boardGame} className="w-full h-full object-cover opacity-20" alt="Catan Tablero Real" />
          <div className="absolute inset-0 bg-gradient-to-b from-[#1a1a1a] via-[#1a1a1a]/50 to-[#1a1a1a]" />
        </div>
        
        <div className="relative z-10 max-w-4xl mx-auto px-6 text-center">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-50px" }}
            transition={{ duration: 0.6 }}
          >
            <Shield className="w-16 h-16 mx-auto text-catan-blue mb-6" />
            <h2 className="text-5xl font-bold mb-8">El Juego de Mesa por Excelencia</h2>
            <p className="text-xl text-white/70 font-light leading-relaxed mb-12">
              Lleva más de 25 años cautivando a millones de jugadores en todo el mundo. Ahora, toda la magia del multijugador asimétrico llega a tu navegador sin instalar nada.
            </p>
            
            <div className="flex flex-wrap justify-center gap-6">
              <div className="flex flex-col items-center">
                <span className="text-4xl font-black text-catan-gold">100M+</span>
                <span className="text-sm text-white/50 uppercase tracking-wider mt-1">Copias Físicas</span>
              </div>
              <div className="w-px h-16 bg-white/20 hidden sm:block" />
              <div className="flex flex-col items-center">
                <span className="text-4xl font-black text-catan-red">4</span>
                <span className="text-sm text-white/50 uppercase tracking-wider mt-1">Jugadores Clásicos</span>
              </div>
              <div className="w-px h-16 bg-white/20 hidden sm:block" />
              <div className="flex flex-col items-center">
                <span className="text-4xl font-black text-catan-blue">10</span>
                <span className="text-sm text-white/50 uppercase tracking-wider mt-1">Puntos de Victoria</span>
              </div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* CTA Footer Section */}
      <section id="comenzar" className="py-24 bg-[#111] relative overflow-hidden">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-catan-red/5 rounded-full blur-[100px] pointer-events-none" />
        
        <div className="max-w-4xl mx-auto px-6 text-center relative z-10">
          <h2 className="text-4xl md:text-6xl font-black mb-6">¿Estás listo para fundar?</h2>
          <p className="text-lg text-white/60 mb-10 max-w-2xl mx-auto">
            Únete a la beta online. Crea salas privadas o únete a partidas públicas. No se permiten tramposos, solo estrategas de recursos.
          </p>
          <button className="bg-white hover:bg-gray-100 text-black px-12 py-5 rounded-full font-bold text-lg hover:scale-105 transition-all shadow-[0_0_30px_rgba(255,255,255,0.3)]">
            CREAR SALA AHORA
          </button>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-black py-8 border-t border-white/10">
        <div className="max-w-7xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between text-white/40 text-sm">
          <div className="flex items-center gap-2 mb-4 md:mb-0">
            <Hexagon className="w-5 h-5" />
            <span>© 2026 Catan Online Project.</span>
          </div>
          <div className="flex gap-6">
            <a href="#" className="hover:text-catan-gold transition-colors">Términos</a>
            <a href="#" className="hover:text-catan-gold transition-colors">Privacidad</a>
            <a href="#" className="hover:text-catan-gold transition-colors">Contacto</a>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;
